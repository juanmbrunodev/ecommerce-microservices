package com.jmb.microservices.core.recommendation.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.OptimisticLockingFailureException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Persistence test class for {@link RecommendationRepository}.
 */
@DataMongoTest
public class RecommendationRepositoryTest extends MongoDBTestBase {

    private static final int PERSISTED_PRODUCT_ID = 1;
    private static final String RECOMMENDATION_AUTHOR = "an_author";
    private static final int RECOMMENDATION_ID = 1;
    private static final int RATE = 1;
    @Autowired
    private RecommendationRepository repository;

    private RecommendationEntity savedEntity;

    @BeforeEach
    public void setup() {
        repository.deleteAll();

        //Insert an entity to retrieve in some CRUD test cases
        var entity = new RecommendationEntity(PERSISTED_PRODUCT_ID, RECOMMENDATION_ID, RECOMMENDATION_AUTHOR,
                RATE, "content");
        savedEntity = repository.save(entity);

        assertRecommendationsAreEqual(entity, savedEntity);
        assertEquals(1, repository.count());
    }

    @Test
    @DisplayName("Test that a RecommendationEntity is successfully created with the Repository")
    public void testCreate() {
        var entity = new RecommendationEntity(2, RECOMMENDATION_ID, RECOMMENDATION_AUTHOR,
                RATE, "content");
        repository.save(entity);
        var foundEntity = repository.findByProductId(entity.getProductId()).get(0);
        assertRecommendationsAreEqual(foundEntity, entity);
    }

    @Test
    @DisplayName("Test that a RecommendationEntity is successfully updated")
    public void testUpdate() {
        String changedAuthor = "changed_author";
        savedEntity.setAuthor(changedAuthor);
        repository.save(savedEntity);
        var foundEntity = repository.findByProductId(savedEntity.getProductId()).get(0);
        assertEquals(changedAuthor, foundEntity.getAuthor());
    }

    @Test
    @DisplayName("Test that a RecommendationEntity is successfully deleted with the Repository")
    public void testDelete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getProductId()));
    }

    @Test
    @DisplayName("Test that a RecommendationEntity is successfully retrieved by ProductId")
    public void testFindByProductId() {
        var foundEntity = repository.findByProductId(savedEntity.getProductId()).get(0);
        assertRecommendationsAreEqual(savedEntity, foundEntity);
    }

    @Test
    @DisplayName("Test that many RecommendationEntities are inserted successfully")
    public void testDuplicatedKeyException() {
        var entity = new RecommendationEntity(PERSISTED_PRODUCT_ID, RECOMMENDATION_ID + 1, RECOMMENDATION_AUTHOR,
                RATE, "content");
        repository.save(entity);
        assertEquals(2, repository.findByProductId(PERSISTED_PRODUCT_ID).size());
    }

    @Test
    @DisplayName("Test that if a RecommendationEntity was updated before an outdated update takes place, optimistic error is thrown")
    void testOptimisticLocking() {
        //Retrieve a saved entity
        var savedEntityOutdated = repository.findByProductId(savedEntity.getProductId()).get(0);

        //Change the same retrieve before, perform and push changes on it to DB
        savedEntity.setAuthor("changed_author_1");
        repository.save(savedEntity);

        //Exception is expected as the first retrieved entity version is now outdated after above change
        assertThrows(OptimisticLockingFailureException.class, () -> {
            savedEntityOutdated.setAuthor("outdated_author_change");
            repository.save(savedEntityOutdated);
        });

        //Assert the last valid state of the entity is the one with the first change
        var savedEntityLast = repository.findByProductId(savedEntity.getProductId()).get(0);
        assertEquals("changed_author_1", savedEntityLast.getAuthor());
    }

    private void assertRecommendationsAreEqual(RecommendationEntity entity, RecommendationEntity savedEntity) {
        assertEquals(entity.getProductId(), savedEntity.getProductId());
        assertEquals(entity.getRecommendationId(), savedEntity.getRecommendationId());
        assertEquals(entity.getAuthor(), savedEntity.getAuthor());
        assertEquals(entity.getRate(), savedEntity.getRate());
        assertEquals(entity.getContent(), savedEntity.getContent());
    }
}
