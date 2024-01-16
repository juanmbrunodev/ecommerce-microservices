package com.jmb.core.review;

import org.springframework.web.bind.annotation.*;

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
    Review createReview(@RequestBody Review body);

    @GetMapping(
        value    = "/review",
        produces = "application/json")
    List<Review> getReviews(@RequestParam(value = "productId", required = true) int productId);

    @DeleteMapping(value = "/review")
    void deleteReviews(@RequestParam(value = "productId", required = true)  int productId);
}
