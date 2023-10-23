package com.jmb.microservices.core.recommendation.mapper;

import com.jmb.core.recommendation.Recommendation;
import com.jmb.microservices.core.recommendation.persistence.RecommendationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    public RecommendationEntity mapApiToEntity(Recommendation model);

    @Mapping(target = "serviceAddress", ignore = true)
    public Recommendation mapEntityToApi(RecommendationEntity entity);
}
