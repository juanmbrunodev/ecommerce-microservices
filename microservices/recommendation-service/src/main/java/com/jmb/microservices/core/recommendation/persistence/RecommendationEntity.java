package com.jmb.microservices.core.recommendation.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Class represents an entity linked to a MongoDB collection of Recommendation documents.
 * <p>
 * The version field is used to implement optimistic locking, allowing Spring Data to verify that updates of an entity in
 * the database do not overwrite a concurrent update. If the value of the version field stored in the database is higher
 * than the value of the version field in an update request, it indicates that the update is performed on stale data—the
 * information to be updated has been updated by someone else since it was read from the database.
 * </p>
 * @author JuanMBruno.
 */

@Document(collection = "recommendations")
/*
* The numbers 1 in this context indicate the direction of the index.
* Specifically:

1 means ascending order.
-1 would mean descending order
* (Note that the index name is not included in the compound index
* definition).
* */
@CompoundIndex(name = "prod-rec-id", unique = true,
        def = "{'productId': 1, 'recommendationId' : 1}")
public class RecommendationEntity {

    @Id
    private String id;

    @Version
    private int version;

    private int productId;
    private int recommendationId;
    private String author;
    private int rate;
    private String content;

    public RecommendationEntity(int productId, int recommendationId, String author, int rate, String content) {
        this.productId = productId;
        this.recommendationId = recommendationId;
        this.author = author;
        this.rate = rate;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public int getProductId() {
        return productId;
    }

    public int getRecommendationId() {
        return recommendationId;
    }

    public String getAuthor() {
        return author;
    }

    public int getRate() {
        return rate;
    }

    public String getContent() {
        return content;
    }
}
