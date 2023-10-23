package com.jmb.microservices.core.product.mapper;

import com.jmb.core.product.Product;
import com.jmb.microservices.core.product.persistence.ProductEntity;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductMapperTest {

    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void testMapApiToEntity() {
        var productApi = new Product(1, "aProduct", 10, "aServicessAddressIgnored");
        var mappedEntity = productMapper.mapApiToEntity(productApi);
        assertEquals(mappedEntity.getProductId(), productApi.getProductId());
        assertEquals(mappedEntity.getName(), productApi.getName());
        assertEquals(mappedEntity.getWeight(), productApi.getWeight());
    }

    @Test
    void testEntityToApi() {
        var entity = new ProductEntity(1, "aProduct", 10);
        var mappedProductApi = productMapper.mapEntityToApi(entity);
        assertEquals(mappedProductApi.getProductId(), entity.getProductId());
        assertEquals(mappedProductApi.getName(), entity.getName());
        assertEquals(mappedProductApi.getWeight(), entity.getWeight());
        assertNull(mappedProductApi.getServiceAddress());
    }
}
