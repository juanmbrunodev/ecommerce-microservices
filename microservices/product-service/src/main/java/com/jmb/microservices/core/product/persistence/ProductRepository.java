package com.jmb.microservices.core.product.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.Optional;

public interface ProductRepository extends PagingAndSortingRepository<ProductEntity, Integer>,
        CrudRepository<ProductEntity, Integer> {
    Optional<ProductEntity> findByProductId(int productId);
}
