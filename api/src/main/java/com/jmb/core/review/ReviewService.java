package com.jmb.core.review;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Describing a RESTful API in a Java interface instead of directly in the Java class is a good way of separating the
 * API definition from its implementation.
 */
public interface ReviewService {

    @PostMapping(
        value    = "/review",
        consumes = "application/json",
        produces = "application/json")
    Mono<Review> createReview(@RequestBody Review body);

    @GetMapping(
        value    = "/review",
        produces = "application/json")
    Flux<Review> getReviews(@RequestParam(value = "productId", required = true) int productId);

    @DeleteMapping(value = "/review")
    Mono<Void> deleteReviews(@RequestParam(value = "productId", required = true)  int productId);
}
