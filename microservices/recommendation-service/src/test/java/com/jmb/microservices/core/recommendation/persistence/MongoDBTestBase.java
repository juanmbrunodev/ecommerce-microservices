package com.jmb.microservices.core.recommendation.persistence;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

/**
 * Sometimes it might be useful to define a container that is only started once for several test classes.
 * There is no special support for this use case provided by the Testcontainers extension.

 * Instead, a base test class is loaded.
 * This way the singleton container is started only once when the base class is loaded.
 */
public abstract class MongoDBTestBase {

   private static final int MONGODB_PORT = 27017;

   static final MongoDBContainer DATABASE_MONGODB;

   static {
       DATABASE_MONGODB = new MongoDBContainer("mongo:4.4.2")
               .withReuse(true);
       DATABASE_MONGODB.start();
   }

    //Set properties based on the container for MongoDB into the spring test properties.
    @DynamicPropertySource
    static void setSpringTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", DATABASE_MONGODB::getHost);
        registry.add("spring.data.mongodb.port",
                () -> DATABASE_MONGODB.getMappedPort(MONGODB_PORT));
        registry.add("spring.data.mongodb.database", () -> "test");
        //Needs to be added for spring tests to create the index
        registry.add("spring.data.mongodb.auto-index-creation", () -> true);
    }

}
