package com.jmb.microservices.core.review.mapper;

import com.jmb.core.review.Review;
import com.jmb.microservices.core.review.persistence.ReviewEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-01-02T19:23:27+0100",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 18.0.2.1 (Oracle Corporation)"
)
@Component
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public Review entityToApi(ReviewEntity reviewEntity) {
        if ( reviewEntity == null ) {
            return null;
        }

        int productId = 0;
        int reviewId = 0;
        String author = null;
        String subject = null;
        String content = null;

        productId = reviewEntity.getProductId();
        reviewId = reviewEntity.getReviewId();
        author = reviewEntity.getAuthor();
        subject = reviewEntity.getSubject();
        content = reviewEntity.getContent();

        String serviceAddress = null;

        Review review = new Review( productId, reviewId, author, subject, content, serviceAddress );

        return review;
    }

    @Override
    public ReviewEntity apiToEntity(Review review) {
        if ( review == null ) {
            return null;
        }

        int productId = 0;
        int reviewId = 0;
        String author = null;
        String subject = null;
        String content = null;

        productId = review.getProductId();
        reviewId = review.getReviewId();
        author = review.getAuthor();
        subject = review.getSubject();
        content = review.getContent();

        ReviewEntity reviewEntity = new ReviewEntity( productId, reviewId, author, subject, content );

        return reviewEntity;
    }
}
