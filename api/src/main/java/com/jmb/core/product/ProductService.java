package com.jmb.core.product;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Describing a RESTful API in a Java interface instead of directly in the Java class is a good way of separating the
 * API definition from its implementation.
 */
public interface ProductService {

    String CONTENT_TYPE_JSON = "application/json";

    @GetMapping(
        value    = "/product/{productId}",
        produces = CONTENT_TYPE_JSON)
    Mono<Product> getProduct(@PathVariable int productId);

    //If any changes, need to add new operations for the API, make sure OpenAPI documents them in the composite Service
    @PostMapping(
            value = "/product",
            produces = CONTENT_TYPE_JSON,
            consumes = CONTENT_TYPE_JSON
    )
    Mono<Product> createProduct(@RequestBody Product product);

    @DeleteMapping("/product/{productId}")
    Mono<Void> deleteProduct(@PathVariable int productId);
}
