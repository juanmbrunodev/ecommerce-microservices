package com.jmb.microservices.core.review.persistence;

import jakarta.persistence.*;

/**
 * Class represents an entity linked to a SQL Table of Review documents.
 * <p>
 * The version field is used to implement optimistic locking, allowing Spring Data to verify that updates of an entity in
 * the database do not overwrite a concurrent update. If the value of the version field stored in the database is higher
 * than the value of the version field in an update request, it indicates that the update is performed on stale data—the
 * information to be updated has been updated by someone else since it was read from the database.
 * </p>
 * @author JuanMBruno.
 */
@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "reviews_unique_idx", unique = true,
                columnList = "productId,reviewId")
})
public class ReviewEntity {

    @Id @GeneratedValue
    private int id;

    @Version
    private int version;

    private int productId;
    private int reviewId;
    private String author;
    private String subject;
    private String content;

    public ReviewEntity(int productId, int reviewId, String author, String subject, String content) {
        this.productId = productId;
        this.reviewId = reviewId;
        this.author = author;
        this.subject = subject;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public int getProductId() {
        return productId;
    }

    public int getReviewId() {
        return reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }
}
