# Assign a base image
FROM amazoncorretto:17-alpine-jdk

# Maintainer / author
LABEL maintainer="golibjon_davlatov@epam.com"

# Specify the argument for the jar file
ARG JAR_FILE=build/libs/resource-processor-0.0.1-SNAPSHOT.jar

# Copy the jar file to the docker image
COPY ${JAR_FILE} app.jar

# Expose the server port
EXPOSE 8080

# Set the start point
ENTRYPOINT ["java","-jar","/app.jar"]