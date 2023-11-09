package com.jmb.microservices.core.recommendation.mapper;

import com.jmb.core.recommendation.Recommendation;
import com.jmb.microservices.core.recommendation.persistence.RecommendationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mappings({
            @Mapping(target = "rate", source = "entity.rate"),
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Recommendation mapEntityToApi(RecommendationEntity entity);

    @Mappings({
            @Mapping(target = "rate", source = "api.rate"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    RecommendationEntity apiToEntity(Recommendation api);
}
