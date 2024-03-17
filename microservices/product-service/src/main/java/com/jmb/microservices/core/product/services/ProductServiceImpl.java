package com.jmb.microservices.core.product.services;

import com.jmb.microservices.core.product.mapper.ProductMapper;
import com.jmb.microservices.core.product.persistence.ProductRepository;
import com.mongodb.DuplicateKeyException;
import io.netty.handler.logging.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.jmb.core.product.Product;
import com.jmb.core.product.ProductService;
import com.jmb.util.exceptions.InvalidInputException;
import com.jmb.util.exceptions.NotFoundException;
import com.jmb.util.http.ServiceUtil;
import reactor.core.publisher.Mono;

@RestController
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ServiceUtil serviceUtil, ProductRepository productRepository,
                              ProductMapper productMapper) {
        this.serviceUtil = serviceUtil;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Mono<Product> getProduct(int productId) {
        LOG.debug("/product return the found product for productId={}", productId);
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        return productRepository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " + productId)))
                .log("Found product for productId: " + productId)
                .map(productMapper::mapEntityToApi)
                .map(product -> {
                    product.setServiceAddress(serviceUtil.getServiceAddress());
                    return product;
                });
    }

    @Override
    public Mono<Product> createProduct(Product body) {
        if (body.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: " + body.getProductId());
        }
        var productEntity = productMapper.mapApiToEntity(body);
        return productRepository.save(productEntity)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId()))
                .log("Created product for" + body.toString())
                .map(productMapper::mapEntityToApi);

    }

    @Override
    public Mono<Void> deleteProduct(int productId) {
        LOG.debug("/product delete request received for productId={}", productId);
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        return productRepository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " + productId)))
                .log("Found product for productId: " + productId)
                .flatMap(productRepository::delete);
    }
}
