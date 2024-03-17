package com.jmb.microservices.core.product.persistence;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.dao.DuplicateKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.test.StepVerifier;


@DataMongoTest
class ProductRepositoryTest extends MongoDBTestBase {

    private static final int PERSISTED_PRODUCT_ID = 1;
    private static final String PRODUCT_NAME = "a_product";
    private static final int PRODUCT_WEIGHT = 1;

    private ProductEntity savedEntity;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setupDB() {
        productRepository.deleteAll().block();

        //Insert for Retrieving in some CRUD test cases
        var entity = buildEntity(PERSISTED_PRODUCT_ID);
        StepVerifier.create(productRepository.save(entity))
                .expectNextMatches(createdEntity -> {
                    savedEntity = createdEntity;
                    return assertProductsAreEqual(entity, savedEntity);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Test that a ProductEntity is successfully created with the Repository")
    void testCreate() {
        var entity = buildEntity(2);
        productRepository.save(entity).block();
        StepVerifier.create(productRepository.findByProductId(entity.getProductId()))
                .expectNextMatches(foundEntity -> assertProductsAreEqual(entity, foundEntity))
                .verifyComplete();
    }

    @Test
    @DisplayName("Test that a ProductEntity is successfully updated with the Repository")
    void testUpdate() {
        savedEntity.setName("name2");
        StepVerifier.create(productRepository.save(savedEntity))
                .expectNextMatches(updatedEntity -> updatedEntity.getName().equals("name2"))
                .verifyComplete();

        StepVerifier.create(productRepository.findByProductId(savedEntity.getProductId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1
                                && foundEntity.getName().equals("name2"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Test that a ProductEntity is successfully deleted with the Repository")
    void testDelete() {
        StepVerifier.create(productRepository.delete(savedEntity))
                .verifyComplete();
        StepVerifier.create(productRepository.existsById(savedEntity.getProductId()))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("Test that a ProductEntity is successfully retrieved by Id with the Repository")
    void testGetProductById() {
        StepVerifier.create(productRepository.findByProductId(savedEntity.getProductId()))
                .expectNextMatches(foundEntity -> assertProductsAreEqual(savedEntity, foundEntity))
                .verifyComplete();
    }

    @Test
    @DisplayName("Test that an error is generated when an item is saved with a duplicate key")
    void testDuplicateKeyError() {
        ProductEntity duplicatedProductIdEntity = new ProductEntity(savedEntity.getProductId(), "name", 1);
        StepVerifier.create(productRepository.save(duplicatedProductIdEntity))
                .expectError(DuplicateKeyException.class)
                .verify();
    }

    @Test
    @DisplayName("Test that if a product was updated before an outdated update takes place, optimistic error is thrown")
    void testOptimisticLocking() {
        //Retrieve a saved entity
        var savedEntityOutdated = productRepository.findByProductId(savedEntity.getProductId()).block();

        //Change the same retrieve before, perform and push changes on it to DB
        savedEntity.setName("changed_name_1");
        productRepository.save(savedEntity).block();

        //Exception is expected as the first retrieved entity version is now outdated after above change
        StepVerifier.create(productRepository.save(savedEntityOutdated))
                .expectError(OptimisticLockingFailureException.class)
                .verify();


        //Assert the last valid state of the entity is the one with the first change
        StepVerifier.create(productRepository.findByProductId(savedEntity.getProductId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1
                                && foundEntity.getName().equals("changed_name_1"))
                .verifyComplete();
    }


    private boolean assertProductsAreEqual(ProductEntity entity, ProductEntity actualEntity) {
        assertEquals(entity.getId(), actualEntity.getId());
        assertEquals(entity.getVersion(), actualEntity.getVersion());
        assertEquals(entity.getProductId(), actualEntity.getProductId());
        assertEquals(entity.getName(), actualEntity.getName());
        assertEquals(entity.getWeight(), actualEntity.getWeight());
        return true;
    }

    private ProductEntity buildEntity(int productId) {
        return new ProductEntity(productId, PRODUCT_NAME, PRODUCT_WEIGHT);
    }
}
