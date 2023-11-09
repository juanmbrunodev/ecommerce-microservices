package com.jmb.microservices.core.recommendation.mapper;

import com.jmb.core.recommendation.Recommendation;
import com.jmb.microservices.core.recommendation.persistence.RecommendationEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-11-09T17:02:16+0100",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 18.0.2.1 (Oracle Corporation)"
)
@Component
public class RecommendationMapperImpl implements RecommendationMapper {

    @Override
    public Recommendation mapEntityToApi(RecommendationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Recommendation recommendation = new Recommendation();

        recommendation.setRate( entity.getRate() );
        recommendation.setProductId( entity.getProductId() );
        recommendation.setRecommendationId( entity.getRecommendationId() );
        recommendation.setAuthor( entity.getAuthor() );
        recommendation.setContent( entity.getContent() );

        return recommendation;
    }

    @Override
    public RecommendationEntity apiToEntity(Recommendation api) {
        if ( api == null ) {
            return null;
        }

        int rate = 0;
        int productId = 0;
        int recommendationId = 0;
        String author = null;
        String content = null;

        rate = api.getRate();
        productId = api.getProductId();
        recommendationId = api.getRecommendationId();
        author = api.getAuthor();
        content = api.getContent();

        RecommendationEntity recommendationEntity = new RecommendationEntity( productId, recommendationId, author, rate, content );

        return recommendationEntity;
    }
}
