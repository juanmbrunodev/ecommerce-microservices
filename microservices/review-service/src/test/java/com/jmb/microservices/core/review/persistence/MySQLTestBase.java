package com.jmb.microservices.core.review.persistence;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

/**
 * Test base class that uses test containers aiming at a MySQL image for repository/integration type of tests.
 */
public abstract class MySQLTestBase {

    private static MySQLContainer database = new MySQLContainer("mysql:5.7.32");

    static {
        database.start();
    }

    //Set properties based on the container for MongoDB into the spring test properties.
    @DynamicPropertySource
    static void setSpringTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
    }
}
