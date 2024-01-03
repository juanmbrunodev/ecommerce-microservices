package com.jmb.microservices.core.recommendation;

import com.jmb.microservices.core.recommendation.persistence.MongoDBTestBase;
import com.jmb.microservices.core.recommendation.persistence.RecommendationEntity;
import com.jmb.microservices.core.recommendation.persistence.RecommendationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class RecommendationServiceApplicationTests extends MongoDBTestBase {

    @Autowired
    private WebTestClient client;

    @Autowired
    private RecommendationRepository repository;

    @Test
    public void getRecommendationsByProductId() {

        int productId = 1;

        insertRecommendation(productId, 1);
        insertRecommendation(productId, 2);

        client.get()
                .uri("/recommendation?productId=" + productId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].productId").isEqualTo(productId)
                .jsonPath("$[1].productId").isEqualTo(productId);
    }

    @Test
    public void getRecommendationsMissingParameter() {

        client.get()
                .uri("/recommendation")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/recommendation");
    }

    @Test
    public void getRecommendationsInvalidParameter() {

        client.get()
                .uri("/recommendation?productId=no-integer")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/recommendation")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getRecommendationsNotFound() {

        int productIdNotFound = 113;

        client.get()
                .uri("/recommendation?productId=" + productIdNotFound)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void getRecommendationsInvalidParameterNegativeValue() {

        int productIdInvalid = -1;

        client.get()
                .uri("/recommendation?productId=" + productIdInvalid)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/recommendation")
                .jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
    }

    private void insertRecommendation(int productId, int recommendationId) {
        repository.save(new RecommendationEntity(productId, recommendationId, "Author 1", 1,
                "Content 1"));
    }
}
