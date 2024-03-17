package com.jmb.microservices.core.recommendation.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface RecommendationRepository extends ReactiveCrudRepository<RecommendationEntity, Integer> {

    /**
     * One Product might have many recommendations
     */
    Flux<RecommendationEntity> findByProductId(int productId);
}
