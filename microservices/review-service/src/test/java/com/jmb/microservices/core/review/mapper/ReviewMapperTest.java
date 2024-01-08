package com.jmb.microservices.core.review.mapper;

import com.jmb.core.review.Review;
import com.jmb.microservices.core.review.persistence.ReviewEntity;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReviewMapperTest {

    ReviewMapper reviewMapper = Mappers.getMapper(ReviewMapper.class);

    @Test
    public void testEntityToApi() {

        var entity = new ReviewEntity(1, 2, "a", "s", "c");
        var api = reviewMapper.entityToApi(entity);

        assertEquals(api.getProductId(), entity.getProductId());
        assertEquals(api.getReviewId(), entity.getReviewId());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getSubject(), entity.getSubject());
        assertEquals(api.getContent(), entity.getContent());
    }

    @Test
    public void testApiToEntity() {
        var api = new Review(1, 2, "a", "s", "c", "n");
        var entity = reviewMapper.apiToEntity(api);

        assertEquals(entity.getProductId(), api.getProductId());
        assertEquals(entity.getReviewId(), api.getReviewId());
        assertEquals(entity.getAuthor(), api.getAuthor());
        assertEquals(entity.getSubject(), api.getSubject());
        assertEquals(entity.getContent(), api.getContent());
    }
}
