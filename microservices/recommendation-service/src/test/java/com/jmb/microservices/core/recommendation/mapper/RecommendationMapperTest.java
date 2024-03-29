package com.jmb.microservices.core.recommendation.mapper;

import com.jmb.core.recommendation.Recommendation;
import com.jmb.microservices.core.recommendation.persistence.RecommendationEntity;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecommendationMapperTest {

    private final RecommendationMapper mapper = Mappers.getMapper(RecommendationMapper.class);

    @Test
    public void testEntityToApi() {

        var entity = new RecommendationEntity(1, 1, "anAuthor", 5, "aContent");
        var api = mapper.entityToApi(entity);

        assertEquals(api.getRecommendationId(), entity.getRecommendationId());
        assertEquals(api.getProductId(), entity.getProductId());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getRate(), entity.getRate());
    }

    @Test
    public void testApiToEntity() {
        var api = new Recommendation(1, 1, "anAuthor", 5, "aContent",
                "serviceAddress");
        var entity = mapper.apiToEntity(api);

        assertEquals(entity.getProductId(), api.getProductId());
        assertEquals(entity.getRecommendationId(), api.getRecommendationId());
        assertEquals(entity.getAuthor(), api.getAuthor());
        assertEquals(entity.getRate(), api.getRate());
        assertEquals(entity.getContent(), api.getContent());
    }
}
