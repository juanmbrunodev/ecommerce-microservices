package com.jmb.microservices.core.recommendation.services;

import com.jmb.microservices.core.recommendation.mapper.RecommendationMapper;
import com.jmb.microservices.core.recommendation.persistence.RecommendationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.jmb.core.recommendation.Recommendation;
import com.jmb.core.recommendation.RecommendationService;
import com.jmb.util.exceptions.InvalidInputException;
import com.jmb.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final RecommendationRepository repository;

    private final RecommendationMapper recommendationMapper;

    @Autowired
    public RecommendationServiceImpl(ServiceUtil serviceUtil, RecommendationRepository repository,
                                     RecommendationMapper recommendationMapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.recommendationMapper = recommendationMapper;
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        //Retrieve recommendations by productId and map to API (DTO) objects
        var recommendations = repository.findByProductId(productId)
                .stream()
                .map(recommendationMapper::entityToApi)
                .toList();

        recommendations.forEach(recommendation -> recommendation.setServiceAddress(serviceUtil.getServiceAddress()));
        // Return if we found anything
        if (recommendations.isEmpty()) {
            LOG.debug("/recommendation response size is empty");
            return recommendations;
        }
        LOG.debug("/recommendation response size: {}", recommendations.size());
        return recommendations;
    }
}
