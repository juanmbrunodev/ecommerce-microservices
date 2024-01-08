package com.jmb.microservices.core.product.persistence;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.ASC;

import org.springframework.dao.DuplicateKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.stream.Collectors;

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
        productRepository.deleteAll();

        //Insert for Retrieving in some CRUD test cases
        var entity = buildEntity(PERSISTED_PRODUCT_ID);
        savedEntity = productRepository.save(entity);

        assertProductsAreEqual(entity, savedEntity);
    }

    @Test
    @DisplayName("Test that a ProductEntity is successfully created with the Repository")
    void testCreate() {
        var entity = buildEntity(2);
        productRepository.save(entity);
        var foundEntity = productRepository.findByProductId(entity.getProductId()).get();

        assertProductsAreEqual(entity, foundEntity);
        assertEquals(2, productRepository.count());
    }

    @Test
    @DisplayName("Test that a ProductEntity is successfully updated with the Repository")
    void testUpdate() {
        String changedName = "changed_name";
        savedEntity.setName(changedName);
        productRepository.save(savedEntity);
        var foundEntity = productRepository.findByProductId(savedEntity.getProductId()).get();
        assertEquals(changedName, foundEntity.getName());
    }


    @Test
    @DisplayName("Test that a ProductEntity is successfully deleted with the Repository")
    void testDelete() {
        productRepository.delete(savedEntity);
        assertFalse(productRepository.existsById(savedEntity.getProductId()));
    }

    @Test
    @DisplayName("Test that a ProductEntity is successfully retrieved by Id with the Repository")
    void testGetProductById() {
        var foundEntityOptional = productRepository.findByProductId(savedEntity.getProductId());

        assertTrue(foundEntityOptional.isPresent());
        assertProductsAreEqual(foundEntityOptional.get(), savedEntity);
    }

    @Test
    @DisplayName("Test that an error is generated when an item is saved with a duplicate key")
    void testDuplicateKeyError() {
        assertThrows(DuplicateKeyException.class, () -> {
           var duplicatedKeyEntity = buildEntity(savedEntity.getProductId());
           productRepository.save(duplicatedKeyEntity);
        });
    }

    @Test
    @DisplayName("Test that if a product was updated before an outdated update takes place, optimistic error is thrown")
    void testOptimisticLocking() {
        //Retrieve a saved entity
        var savedEntityOutdated = productRepository.findByProductId(savedEntity.getProductId()).get();

        //Change the same retrieve before, perform and push changes on it to DB
        savedEntity.setName("changed_name_1");
        productRepository.save(savedEntity);

        //Exception is expected as the first retrieved entity version is now outdated after above change
        assertThrows(OptimisticLockingFailureException.class, () -> {
            savedEntityOutdated.setName("outdated_name_change");
            productRepository.save(savedEntityOutdated);
        });

        //Assert the last valid state of the entity is the one with the first change
        var savedEntityLast = productRepository.findByProductId(savedEntity.getProductId()).get();
        assertEquals(1, savedEntityLast.getVersion());
        assertEquals("changed_name_1", savedEntityLast.getName());
    }

    @Test
    @DisplayName("Test pagination works as expected while retrieving many products")
    void testPagination() {

        //Delete previously inserted, for other test cases, 'savedEntity' ProductEntity
        productRepository.deleteAll();

        //Create some product entities and persist them
        var newProducts = rangeClosed(1001, 1010)
                .mapToObj(this::buildEntity)
                .toList();

        productRepository.saveAll(newProducts);

        //Create the Paging Request object: initial page, size of 4, asc order, passing key
        Pageable pageRequest = PageRequest.of(0, 4, ASC, "productId");

        pageRequest = assertNextPage(pageRequest, "[1001, 1002, 1003, 1004]", true);
        pageRequest = assertNextPage(pageRequest, "[1005, 1006, 1007, 1008]", true);
        assertNextPage(pageRequest, "[1009, 1010]", false);
    }

    private Pageable assertNextPage(Pageable nextPage, String expectedProductIds, boolean expectsNextPage) {
        Page<ProductEntity> productPage = productRepository.findAll(nextPage);
        assertEquals(expectedProductIds,
                productPage.getContent()
                        .stream()
                        .map(ProductEntity::getProductId).toList().toString());
        assertEquals(expectsNextPage, productPage.hasNext());
        return productPage.nextPageable();
    }

    private void assertProductsAreEqual(ProductEntity entity, ProductEntity actualEntity) {
        assertEquals(entity.getId(), actualEntity.getId());
        assertEquals(entity.getVersion(), actualEntity.getVersion());
        assertEquals(entity.getProductId(), actualEntity.getProductId());
        assertEquals(entity.getName(), actualEntity.getName());
        assertEquals(entity.getWeight(), actualEntity.getWeight());
    }

    private ProductEntity buildEntity(int productId) {
        return new ProductEntity(productId, PRODUCT_NAME, PRODUCT_WEIGHT);
    }
}
