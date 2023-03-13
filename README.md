# ecommerce-microservices

Generic microservices project for an imaginary ecommerce site. The high level design can be depicted as follows:




### Running the application in Docker Compose

First, we build our deployment artifacts with Gradle and then the Docker images with Docker Compose, by running

```
/[root_folder]/gradle build
```

And,

```
docker-compose build
```

Then, we need to verify that we can see our Docker images, as follows:

```
docker images | grep ecommerce-microservices
```

Start up the microservices landscape with the following command:

```
docker-compose up
```

The -d option will make Docker Compose run the containers in detached mode, the same as for Docker:

```
docker-compose up -d
```

The logs can be observed by running:

```
docker-compose logs -f
```

Finally, _the composite service can be queried (and all of them internally)_ by running:

```
curl localhost:8080/product-composite/123 -s | jq .
```

The name is the one defined in the REST controller mapping for the composite service interface (ProductCompositeService.)

To stop docker compose execute:

```
docker compose down
```
