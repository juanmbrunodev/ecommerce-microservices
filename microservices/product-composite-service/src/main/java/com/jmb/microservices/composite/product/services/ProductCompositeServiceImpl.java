package com.jmb.microservices.composite.product.services;

import com.jmb.composite.product.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.jmb.core.product.Product;
import com.jmb.core.recommendation.Recommendation;
import com.jmb.core.review.Review;
import com.jmb.util.exceptions.NotFoundException;
import com.jmb.util.http.ServiceUtil;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The controller calls the different services to retrieve information through the {@link ProductCompositeIntegration}
 * class, and after that it creates and aggregate of the information retrieved, as we filter the information
 * returned from the services and keep a summary of some domain objects (Recommendations summary, etc.)
 */
@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private ProductCompositeIntegration integration;

    @Autowired
    public ProductCompositeServiceImpl(ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public void createProduct(@RequestBody ProductAggregate body) {
        try {
            /**
             * Creates the product, hits the product service url in the backend
             * see {@link ProductCompositeIntegration#createProduct(Product)}
             */
            integration.createProduct(new Product(body.getProductId(), body.getName(), body.getWeight()
                    , null));
            /**
             * Create related recommendations, hits the recommendation service url in the backend
             * see {@link ProductCompositeIntegration#createRecommendation(Recommendation)}
             */
            if (body.getRecommendations() != null) {
                body.getRecommendations().forEach(r -> {
                    Recommendation recommendation = new Recommendation(body.getProductId(), r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent(), null);
                    integration.createRecommendation(recommendation);
                });
            }
            /**
             * Create related reviews, hits the review service url in the backend
             * see {@link ProductCompositeIntegration#createReview(Review)}
             */
            if (body.getReviews() != null) {
                body.getReviews().forEach(r -> {
                    Review review = new Review(body.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent(), null);
                    integration.createReview(review);
                });
            }
        } catch (RuntimeException re) {
            LOG.warn("Error while creating composite Product", re);
            throw re;
        }
    }

    @Override
    public Mono<ProductAggregate> getProduct(@PathVariable int productId) {

        Mono<Product> product = integration.getProduct(productId);
        if (product == null) throw new NotFoundException("No product found for productId: " + productId);

        List<Recommendation> recommendations = integration.getRecommendations(productId);

        List<Review> reviews = integration.getReviews(productId);

        return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }

    @Override
    public void deleteProduct(@PathVariable int productId) {
        var product = integration.getProduct(productId);
        if (product != null) {
            integration.deleteProduct(productId);
            integration.deleteRecommendations(productId);
            integration.deleteReviews(productId);
            LOG.debug("deleteProduct: deleted a product with productId: {}", productId);
        }
    }

    private ProductAggregate createProductAggregate(Product product, List<Recommendation> recommendations, List<Review> reviews, String serviceAddress) {

        // 1. Setup product info
        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        // 2. Copy summary recommendation info, if available
        List<RecommendationSummary> recommendationSummaries = (recommendations == null) ? null :
                recommendations.stream()
                        .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent()))
                        .collect(Collectors.toList());

        // 3. Copy summary review info, if available
        List<ReviewSummary> reviewSummaries = (reviews == null) ? null :
                reviews.stream()
                        .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
                        .collect(Collectors.toList());

        // 4. Create info regarding the involved microservices addresses
        String productAddress = product.getServiceAddress();
        String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
        String recommendationAddress = (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).getServiceAddress() : "";
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
    }
}