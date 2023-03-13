# By default, Spring Boot creates the following folders after extracting a fat-jar file:
#
# dependencies: containing all dependencies as jar-files
# spring-boot-loader: containing Spring Boot classes that know how to start a Spring Boot application
# snapshot-dependencies: containing snapshot dependencies, if any
# application: containing application class files and resources

# To handle the extraction of the fat-jar file in the Dockerfile we use a multi-stage build, meaning that there is a first
# step, named builder, that handles the extraction.

# The second stage builds the actual Docker image that will be used at
# runtime, picking the files as required from the first stage. Using this technique, we can handle all packaging logic in
# the Dockerfile but at the same time keep the size of the final Docker image to a minimum

FROM openjdk:18-alpine as builder
WORKDIR extracted
ADD ./build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM openjdk:18-alpine
WORKDIR application
COPY --from=builder extracted/dependencies/ ./
COPY --from=builder extracted/spring-boot-loader/ ./
COPY --from=builder extracted/snapshot-dependencies/ ./
COPY --from=builder extracted/application/ ./
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
