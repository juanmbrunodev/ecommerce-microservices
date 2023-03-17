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

<br/>

### Running the Bash Integration Tests

To execute the bash script to run integration tests on the product-composite service (and by transitivity in the services
it calls), the following shell script can be executed from the root of the project (sudo is not needed). 

./test-em-all.bash start stop

**WARNING:** Docker must be installed on the host machine that runs this command.

**WARNING 2:** If changes are made to the projects, the images of those might be cached locally in Docker. So it's better
to run the following:

- Delete docker images
- Build the whole landscape or the service of interest.
- Run again the test-em-all.bash script as indicated above.

<br/>

### Accessing the OpenAPI Documentation for the Microservices

The Documentation offers in this project general descriptive information about the API, such as:

- The name, description, version, and contact information for the API
- Terms of usage and license information
- Links to external information regarding the API, if any

_To access the API documentation_:

- Run `docker compose up`, if needed, check before the services containers are not running by executing the following
command `docker ps | grep 'ecommerce'`.
- Then, to browse the OpenAPI documentation, use the embedded Swagger UI viewer. Open the http://localhost:8080/openapi/swagger-ui.html URL in a web browser

