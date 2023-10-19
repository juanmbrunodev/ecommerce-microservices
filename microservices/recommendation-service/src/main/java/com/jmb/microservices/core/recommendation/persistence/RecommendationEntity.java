package com.jmb.microservices.core.recommendation.persistence;

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
public class RecommendationEntity {


}
