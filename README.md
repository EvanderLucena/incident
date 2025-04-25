# Incident Management API

This project is a RESTful API for incident management built with Spring Boot. It provides endpoints to create, update, delete, and retrieve incidents.

## Features

- Create a new incident
- Update an existing incident
- Delete an incident by ID
- List all incidents
- Find an incident by ID
- List the 20 most recent incidents, ordered by creation date (descending)

## Technology Stack

- Java 17+
- Spring Boot
- Spring Data JPA (Hibernate)
- H2 Database (in-memory)
- Maven
- Swagger for API documentation
- JUnit and Mockito for unit testing
- Docker for containerization

## Project Structure

The project follows a clean architecture with separate layers:

- **Controller**: Handles HTTP requests and responses
- **Service**: Contains business logic
- **Repository**: Manages data access
- **DTO**: Data Transfer Objects for input/output
- **Model**: Entity classes

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- Docker (optional)

### Running the Application

#### Using Maven

1. Clone the repository
2. Navigate to the project directory
3. Build the project:
   ```
   mvn clean install
   ```
4. Run the application:
   ```
   mvn spring-boot:run
   ```

#### Using Docker

1. Build the Docker image:
   ```
   docker build -t incident-management .
   ```
2. Run the container:
   ```
   docker-compose up
   ```

### Accessing the Application

- The API will be available at: `http://localhost:8080/api/incidents`
- Swagger UI: `http://localhost:8080/swagger-ui/`
- H2 Database Console: `http://localhost:8080/h2-console`

### H2 Database Access

To access the H2 in-memory database console:

1. Navigate to `http://localhost:8080/h2-console`
2. Use the following connection details:
   - Driver Class: `org.h2.Driver`
   - JDBC URL: `jdbc:h2:mem:incidentdb`
   - Username: `sa`
   - Password: `password`

#### Troubleshooting H2 Connection

If you're having trouble connecting to the H2 database:

- Ensure the application is running
- Verify the connection settings match exactly as specified above
- If using Chrome, ensure that third-party cookies are allowed for localhost
- Try using a different browser if issues persist

## API Endpoints

- `POST /api/incidents` - Create a new incident
- `GET /api/incidents/{id}` - Get an incident by ID
- `GET /api/incidents` - Get all incidents
- `GET /api/incidents/latest` - Get the 20 most recent incidents
- `PUT /api/incidents/{id}` - Update an incident
- `DELETE /api/incidents/{id}` - Delete an incident
- `PATCH /api/incidents/{id}/status` - Update incident status
- `GET /api/incidents/status/{status}` - Get incidents by status

## Technical Justification

- **Spring Boot**: Simplifies the setup and development of Spring applications with auto-configuration.
- **Spring Data JPA**: Provides a repository abstraction layer that reduces boilerplate code for data access.
- **H2 Database**: Lightweight in-memory database ideal for development and testing environments.
- **Lombok**: Reduces boilerplate code through annotations for getters, setters, constructors, etc.
- **Springfox/Swagger**: Automatically generates comprehensive API documentation.
- **JUnit & Mockito**: Industry standard for unit testing in Java applications.
- **Docker**: Ensures consistent deployment across different environments.

## Development Notes

- The application uses DTOs to separate the API contract from the internal domain model
- Automatic timestamps are implemented for creation and updates
- The repository layer includes custom queries for specific business requirements