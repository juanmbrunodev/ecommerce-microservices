## Review Service

TODO: Complete the info once the service is fully productionize.

### Building the service with Gradle

Simply running the following command should build and run the service (from root directory):

`gradle :microservices:review-service:build`

**Note**: It's important to build before building the docker image _after_ any changes are made to the project, to avoid
having an outdated service version.

### Running the service as an isolated Docker Container

To run this service in an isolated fashion, as a docker image running in a Docker container, the following command can be
run (provided the docker image has been build before with the following command, in the root folder:

`docker build -t review-service microservices/review-service`):

<br/>

Before running the command, specially the port forwarding on a certain port involved, it's usually recommended to check
if other process (i.e.: a kubernetes service exposed through port-forwarding as well in the same port) is already in use;
as depending on the docker version, this conflict might never be reported or informed to the user. For this the following
command can be used:

```
sudo lsof -i -P | grep LISTEN | less
```

_Docker run command to run the image:_

```
docker run --rm -p8080:8080 -e "SPRING_PROFILES_ACTIVE=docker" review-service
```

**Some Clarifications**

- --rm option already; it will tell Docker to clean up the container once we stop the execution from the Terminal using
  Ctrl + C.

- The -p8080:8080 option maps port 8080 in the container to port 8080 in the Docker host, which makes it possible to
  call it from the outside (See Dockerfile)

- With the -e option, we can specify environment variables for the container, which in this case is
  SPRING_PROFILES_ACTIVE=docker

Then, a request to get a single Product can be done as follows (provided _curl_ utility is available from a terminal):

```
curl localhost:8080/review/productId=[review_id]
```

_To run the container in 'detached mode'_:

We can do this by adding the -d option and at the same time giving it a name using the --name option. Giving it a name
is optional, and Docker will generate a name if we don't, but it makes it easier to send commands to the detached
container using a name that we have decided:

`docker run -d -p8080:8080 -e "SPRING_PROFILES_ACTIVE=docker" --name my-rew-srv review-service`

_To see the docker container running logs, the following docker command can be used:_

```
docker logs my-rew-srv -f
```

The -f option tells the command to follow the log output, that is, not end the command when all the current log output
has been written to the Terminal, but also wait for more output.
