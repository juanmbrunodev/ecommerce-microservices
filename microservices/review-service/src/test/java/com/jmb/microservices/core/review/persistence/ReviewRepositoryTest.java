package com.jmb.microservices.core.review.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReviewRepositoryTest extends MySQLTestBase {

    private static final int PERSISTED_PRODUCT_ID = 1;
    private static final int REVIEW_ID = 1;

    @Autowired
    private ReviewRepository repository;

    private ReviewEntity savedEntity;

    @BeforeEach
    public void setup() {
        //Clears the database from previous test cases run and inserts an entity for certain tests cases to use
        repository.deleteAll();
        savedEntity = repository.save(new ReviewEntity(PERSISTED_PRODUCT_ID, REVIEW_ID, "Author",
                "Subject", "Content"));
        //Assert the entity was inserted correctly
        var persistedReviewEntity = repository.findByProductId(PERSISTED_PRODUCT_ID).get(0);
        assertEntitiesEqual(savedEntity, persistedReviewEntity);
    }

    @Test
    @DisplayName("Tests that a ReviewEntity is created successfully")
    public void testCreate() {
        var persistedReviewEntity = repository.save(new ReviewEntity(PERSISTED_PRODUCT_ID + 1, REVIEW_ID, "Author",
                "Subject", "Content"));
        var entities = repository.findByProductId(PERSISTED_PRODUCT_ID + 1);
        assertEntitiesEqual(entities.get(0), persistedReviewEntity);
        //Assert we have now two entites in total
        List<ReviewEntity> allEntities = StreamSupport.stream(repository.findAll().spliterator(), false)
                .toList();
        assertEquals(2, allEntities.size());
    }

    @Test
    @DisplayName("Tests that a ReviewEntity is updated successfully")
    public void testUpdate() {
        String changedAuthor = "changed_author";
        savedEntity.setAuthor(changedAuthor);
        repository.save(savedEntity);
        var entities = repository.findByProductId(PERSISTED_PRODUCT_ID);
        assertEquals(entities.get(0).getAuthor(), changedAuthor);
    }

    @Test
    @DisplayName("Tests that a ReviewEntity is deleted successfully")
    public void testDelete() {
        repository.deleteAll();
        assertEquals(0, repository.findByProductId(PERSISTED_PRODUCT_ID).size());
    }

    @Test
    @DisplayName("Tests that a ReviewEntity is retrieved successfully")
    public void testFindByProductId() {
        var foundEntity = repository.findByProductId(PERSISTED_PRODUCT_ID).get(0);
        assertEntitiesEqual(savedEntity, foundEntity);
    }

    @Test
    @DisplayName("Test that if RecommendationEntities are inserted with same key ir fails")
    public void testDuplicateKeyError() {
        var entity = new ReviewEntity(PERSISTED_PRODUCT_ID, REVIEW_ID, "Author", "Subject", "Content");
        assertThrows(DataIntegrityViolationException.class, () ->repository.save(entity));
    }

    @Test
    @DisplayName("Test that if a Review Entity was updated before an outdated update takes place, optimistic error is thrown")
    public void testOptimisticLockingError() {
        // Store the saved entity in two separate entity objects
        ReviewEntity savedEntityOutdated = repository.findByProductId(PERSISTED_PRODUCT_ID).get(0);

        // Update the entity using the class entity object already persisted for same productID
        savedEntity.setAuthor("Author1");
        repository.save(savedEntity);

        // Update the entity using the second entity object.
        // This should fail since the second entity now holds an old version number, i.e. it is outdated.
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            savedEntityOutdated.setAuthor("Author2");
            repository.save(savedEntityOutdated);
        });

        // Get the updated entity from the database and verify its new sate
        var updatedEntity = repository.findByProductId(PERSISTED_PRODUCT_ID).get(0);
        assertEquals(1, updatedEntity.getVersion());
        assertEquals("Author1", updatedEntity.getAuthor());
    }

    private void assertEntitiesEqual(ReviewEntity entity, ReviewEntity expected) {
        assertEquals(expected.getProductId(), entity.getProductId());
        assertEquals(expected.getReviewId(), entity.getReviewId());
        assertEquals(expected.getAuthor(), entity.getAuthor());
        assertEquals(expected.getSubject(), entity.getSubject());
        assertEquals(expected.getContent(), entity.getContent());
    }

}
