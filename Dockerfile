# Use a base image with Java
FROM openjdk:21-jdk

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/ecommerce-0.0.1-SNAPSHOT.jar app.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
