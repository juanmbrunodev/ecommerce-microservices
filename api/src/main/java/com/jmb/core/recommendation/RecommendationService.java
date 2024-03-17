package com.jmb.core.recommendation;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Describing a RESTful API in a Java interface instead of directly in the Java class is a good way of separating the
 * API definition from its implementation.
 */
public interface RecommendationService {

    @PostMapping(
            value    = "/recommendation",
            consumes = "application/json",
            produces = "application/json")
    Mono<Recommendation> createRecommendation(@RequestBody Recommendation recommendation);

    @GetMapping(
        value    = "/recommendation",
        produces = "application/json")
    Flux<Recommendation> getRecommendations(@RequestParam(value = "productId", required = true) int productId);

    @DeleteMapping(value = "/recommendation")
    Mono<Void> deleteRecommendations(@RequestParam(value = "productId", required = true)  int productId);

}
