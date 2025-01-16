# Framework and Design Principles

This project implements a REST API to handle various requested cases using Spring Boot as the framework to manage customer and order domains in an ecommerce platform. The architecture is designed with Domain Driven Design (DDD) principles in mind, leveraging packaging strategies described in Onion Architecture.
## Domain Independence and Dependency Inversion

To align with DDD principles, I've ensured that the domain layer remains as independent as possible. I've applied dependency inversion to facilitate interactions with the database layer. Even though Spring Boot is used as the framework, I have avoided incorporating Spring-related annotations in the domain layer.

Given that my entity classes are designed as rich entities and I want to keep them independent, I’ve utilized repository adapters to map them into JPA entities and JPA repositories. This separation helps maintain the integrity and independence of the domain layer. So in the future we might change the database without touching domain layer.
## Caching Strategy

Order listing with filters and pagination is implemented as part of the application requirements. To enhance performance, particularly since orders may be accessed frequently, I have incorporated a caching mechanism to efficiently retrieve them. Redis will be used in docker environment and in memory cache will be used locally
## Dynamic Configuration

The application supports dynamic configurations. If any configuration changes are made, there is no need to restart the entire application. Instead, simply calling the endpoint /actuator/refresh after making changes to the configuration will apply the updates seamlessly.
## Messages and Multilingual Support

Messages within the application are managed through properties files, allowing for support for multiple languages. Similar to dynamic configurations, changes to these messages can also be applied dynamically without restarting the application.

## Security
### Authentication
This project uses Spring Security for authentication and authorization. JWT (JSON Web Token) based authentication is implemented to secure the application. With register, login and logout public endpoints, user interactions can be done. And jwt token can be given as part of http header.
Spring Security is configured to protect endpoints, and access is granted based on roles or authorities assigned to the user. Some public endpoints like register, login, and logout are not protected and can be accessed without authentication.
### Authorization
USER Role: Grants access to general, user-level endpoints.
ADMIN Role: Grants access to admin-specific endpoints, which are restricted to users with the admin role.

## Message Queue
This project uses Apache Kafka for asynchronous communication with external and internal systems.

## Payment Webhook
Order Creation and Cancellation: When an order is created or cancelled, the payment process (including payment withdrawal) is triggered.
External Payment API: The payment API is assumed to be external, and custom payment simulators are used to mimic the external payment service and integration purposes.

## Logging
This project uses Logback to store application logs in JSON format for better structure and analysis.
### Future Enhancements
Loki and Grafana: These tools can be integrated to monitor application logs in real-time, providing a more advanced logging and visualization setup for improved monitoring and troubleshooting.

## Testing

In this project, I have implemented a comprehensive testing strategy to ensure the reliability and functionality of the application.
### Unit Testing

For unit testing, I utilized JUnit 5, a powerful framework that allows for the creation of reliable and maintainable tests. Each unit test is designed to isolate components and validate their behavior in various scenarios. This approach helps identify issues at an early stage, ensuring that individual components function correctly before integration.
### Integration Testing

To assess the overall functionality and integration of the application, I employed MockMvc from Spring Boot. MockMvc allows me to simulate HTTP requests and verify the responses from the REST API endpoints. This is particularly useful for testing how the application interacts with a real database, as it ensures that the entire system works cohesively.

Integration tests provide confidence that the various layers of the application—such as controllers, services, and repositories—interact correctly and that the application behaves as expected in a production-like environment.
### Benefits of the Testing Approach

- Early Detection of Bugs: By testing components individually and collectively, I can identify and resolve issues early in the development process.
- Increased Confidence: Comprehensive tests give me confidence that changes made to the codebase do not introduce new bugs, facilitating easier maintenance and updates.
- Documentation: Tests serve as a form of documentation for the expected behavior of the application, making it easier for new developers to understand the system.

Overall, the combination of unit and integration testing in this project contributes to building a robust and maintainable application.
## Dockerized Project

The project consists of two application.yml files: one for local testing and the other for running the application in a Docker environment. With this setup, you can run the application using Docker without relying on additional dependencies.

### Getting Started with Docker

1. Ensure Docker is installed on your machine.
2. Build the Docker image:
   docker build -t your-image-name .
```
docker build -t emce/ecommerce .
```
3. Run the Docker container:
```
docker compose up -d
```
4. Access the application at `http://localhost:8080`.

## API Documentation

This project uses Swagger for API documentation. Swagger provides an interactive interface to explore and test the APIs in the application.
The Swagger UI is available at the following URL when the application is running: `http://localhost:8080/swagger-ui/index.html`

