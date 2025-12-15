# Reflection

[![CI](https://github.com/macintorsten/reflection/workflows/CI/badge.svg)](https://github.com/macintorsten/reflection/actions/workflows/ci.yml)

A Java Spring Boot REST API project with PostgreSQL backend, demonstrating:
- Spring Boot with Java 21
- JPA/Hibernate with PostgreSQL
- Testcontainers for integration testing
- OpenAPI/Swagger documentation
- GitHub Actions CI/CD

## Building and Testing

### Prerequisites
- Java 21
- Maven 3.x
- Docker

### Quick Start

```bash
# Build the project
mvn clean compile

# Run tests (uses Testcontainers - no manual database setup needed)
mvn test

# Run the application (requires database)
docker compose up -d
mvn spring-boot:run
```

The application will be available at http://localhost:8080

### API Endpoints
- `POST /api/samples` - Create a new sample
- `GET /api/samples` - List all samples
- `GET /swagger-ui.html` - Swagger UI documentation
- `GET /v3/api-docs` - OpenAPI specification

## Testing

Tests use Testcontainers to automatically manage PostgreSQL test databases. No manual database setup is required for testing.

```bash
# Run all tests
mvn clean verify
```

## CI/CD

The project uses GitHub Actions for continuous integration. The CI workflow:
- Runs on every push to `main` and all pull requests
- Executes the full test suite with Testcontainers
- Uploads test results as artifacts

See [.github/workflows/ci.yml](.github/workflows/ci.yml) for details.
