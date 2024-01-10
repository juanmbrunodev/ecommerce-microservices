package com.jmb.microservices.core.review.services;

import com.jmb.microservices.core.review.mapper.ReviewMapper;
import com.jmb.microservices.core.review.persistence.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.jmb.core.review.Review;
import com.jmb.core.review.ReviewService;
import com.jmb.util.exceptions.InvalidInputException;
import com.jmb.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ReviewRepository repository;
    private final ReviewMapper reviewMapper;

    @Autowired
    public ReviewServiceImpl(ServiceUtil serviceUtil, ReviewRepository repository,
                             ReviewMapper reviewMapper) {
        this.repository = repository;
        this.reviewMapper = reviewMapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Review createReview(Review review) {
        var reviewEntity = reviewMapper.apiToEntity(review);
        var savedEntity = repository.save(reviewEntity);
        return reviewMapper.entityToApi(savedEntity);
    }

    @Override
    public List<Review> getReviews(int productId) {

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        if (productId == 213) {
            LOG.debug("No reviews found for productId: {}", productId);
            return  new ArrayList<>();
        }

        var list = repository.findByProductId(productId);

        if (list.isEmpty()) {
            LOG.debug("Reviews not found for id provided: {}", productId);
            return  new ArrayList<>();
        }

        LOG.debug("/reviews response size: {}", list.size());

        return list
                .stream()
                .map(reviewMapper::entityToApi).toList();
    }

    @Override
    public void deleteReviews(int productId) {
        repository.deleteAll(repository.findByProductId(productId));
    }
}
