package com.jmb.microservices.core.recommendation.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends CrudRepository<RecommendationEntity, Integer> {

    /**
     * One Product might have many recommendations
     */
    List<RecommendationEntity> findByProductId(int productId);
}
