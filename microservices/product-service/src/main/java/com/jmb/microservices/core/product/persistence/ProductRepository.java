package com.jmb.microservices.core.product.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, Integer> {
    Mono<ProductEntity> findByProductId(int productId);
}
