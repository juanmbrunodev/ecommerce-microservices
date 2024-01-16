package com.jmb.microservices.core.review;

import com.jmb.core.review.Review;
import com.jmb.microservices.core.review.persistence.MySQLTestBase;
import com.jmb.microservices.core.review.persistence.ReviewEntity;
import com.jmb.microservices.core.review.persistence.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import static reactor.core.publisher.Mono.just;
/**
 * Represents an E2E Test for the Review Service.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ReviewServiceApplicationTests extends MySQLTestBase {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ReviewRepository repository;

    @BeforeEach
    public void setupDB() {
        repository.deleteAll();
    }

    @Test
    public void getReviewsByProductId() {

        insertReviewEntityIntoDB();

        int productId = 1;

        client.get()
                .uri("/review?productId=" + productId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].productId").isEqualTo(productId);
    }

    @Test
    public void getReviewsMissingParameter() {

        client.get()
                .uri("/review")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/review");
    }

    @Test
    public void getReviewsInvalidParameter() {

        client.get()
                .uri("/review?productId=no-integer")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/review")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getReviewsNotFound() {

        int productIdNotFound = 213;

        client.get()
                .uri("/review?productId=" + productIdNotFound)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void getReviewsInvalidParameterNegativeValue() {

        int productIdInvalid = -1;

        client.get()
                .uri("/review?productId=" + productIdInvalid)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/review")
                .jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
    }

    @Test
    public void createReviews() {

        int productId = 1;

        var review = new Review(productId, 1, "Author 1", "Subject 1",
                "Content 1", "SA");

        client.post()
                .uri("/review")
                .body(just(review), Review.class)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();

        assertFalse(repository.findByProductId(productId).isEmpty());
    }

    @Test
    public void deleteReviews() {

        insertReviewEntityIntoDB();

        int productId = 1;

        client.delete()
                .uri("/review?productId=" + productId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody();

        //Also assert the review is no longer present in the database
        client.get()
                .uri("/review?productId=" + productId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    private void insertReviewEntityIntoDB() {
        repository.save(new ReviewEntity(1, 1, "Author 1", "Subject 1",
                "Content 1"));
    }
}
