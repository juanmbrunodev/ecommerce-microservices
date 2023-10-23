package com.jmb.microservices.core.recommendation.mapper;

import com.jmb.microservices.core.recommendation.persistence.RecommendationEntity;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecommendationMapperTest {

    private final RecommendationMapper mapper = Mappers.getMapper(RecommendationMapper.class);

    @Test
    public void testEntityToApi() {

        var entity = RecommendationEntity
                .builder()
                .id("1")
                .productId(1)
                .author("anAuthor")
                .rate(5)
                .content("Content")
                .build();
        var api = mapper.mapEntityToApi(entity);
        assertEquals(api.getRecommendationId(), entity.getRecommendationId());
        assertEquals(api.getProductId(), entity.getProductId());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getRate(), entity.getRate());
    }
}
