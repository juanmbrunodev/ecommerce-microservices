package com.jmb.microservices.core.product;

import com.jmb.microservices.core.product.persistence.MongoDBTestBase;
import com.jmb.microservices.core.product.persistence.ProductEntity;
import com.jmb.microservices.core.product.persistence.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
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
@SpringBootTest(webEnvironment=RANDOM_PORT)
class ProductServiceApplicationTests extends MongoDBTestBase {

	@Autowired
	private ProductRepository productRepository;

	//Delete existing entities in TestContainers MongoDB before each test run
	@BeforeEach
	void setupDB() {
		productRepository.deleteAll();
	}

    @Autowired
    private WebTestClient client;

	@Test
	void getProductById() {

		int productId = 1;

		insertProductInDB(productId);

        client.get()
            .uri("/product/" + productId)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.productId").isEqualTo(productId);
	}

	@Test
	void getProductInvalidParameterString() {

        client.get()
            .uri("/product/no-integer")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(BAD_REQUEST)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/product/no-integer")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	void getProductNotFound() {

		int productIdNotFound = 13;

        client.get()
            .uri("/product/" + productIdNotFound)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/product/" + productIdNotFound)
            .jsonPath("$.message").isEqualTo("Product not found for id provided: " + productIdNotFound);
	}

	@Test
	void getProductInvalidParameterNegativeValue() {

        int productIdInvalid = -1;

        client.get()
            .uri("/product/" + productIdInvalid)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/product/" + productIdInvalid)
            .jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
	}

	private void insertProductInDB(int productId) {
		var aProduct = new ProductEntity(productId, "aProductName", 123);
		productRepository.save(aProduct);
	}
}