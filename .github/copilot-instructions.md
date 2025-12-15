# Copilot Instructions for Reflection Repository

## Repository Summary
Java Spring Boot REST API with PostgreSQL backend demonstrating modern Spring Boot patterns, REST API design, and testing with Testcontainers.

**Stack:** Java 21 | Maven | Spring Boot 3.5.x | PostgreSQL 15 | JPA/Hibernate | JUnit 5 + Testcontainers

## Project Purpose
This is a sample REST API project showcasing:
- RESTful API design with Spring Boot
- JPA/Hibernate entity management with PostgreSQL
- Integration testing using Testcontainers
- OpenAPI/Swagger documentation
- CI/CD with GitHub Actions
- Modern Java practices (records, pattern matching, etc.)

## Key Components
- **API Endpoints:** POST/GET `/api/samples` for CRUD operations
- **Entity Layer:** `Sample.java` JPA entity with validation
- **Service Layer:** `SampleService.java` business logic
- **Controller Layer:** `SampleController.java` REST endpoints
- **Repository:** `SampleRepository.java` Spring Data JPA
- **DTO/Mapping:** `SampleDTO.java` and `SampleMapper.java` for data transfer
- **Error Handling:** `GlobalExceptionHandler.java` for consistent error responses
- **API Docs:** OpenAPI/Swagger UI at `/swagger-ui.html`

## 📚 Instruction Index
**Load these files when needed for specific tasks:**

| Task | File |
|------|------|
| **Build, Test, Run** | `.github/instructions/build-and-test.instructions.md` |
| **Code Structure** | `.github/instructions/architecture.instructions.md` |
| **Dep Research** | `.github/instructions/maven-dependency-research.instructions.md` |
| **Authoring** | `.github/instructions/copilot-authoring.instructions.md` |

## Development Workflow
1. **Before Code Changes:** Always run `mvn clean compile` and `mvn test` to establish baseline
2. **Making Changes:** Follow existing patterns (service layer, DTO mapping, validation)
3. **Testing:** Write integration tests using Testcontainers (see `SampleControllerIntegrationTest.java`)
4. **Validation:** Run `mvn clean package` to ensure build succeeds
5. **Manual Testing:** Use `docker compose up -d` + `mvn spring-boot:run` for local testing

## Coding Standards
- Use Java 21 features where appropriate (records, pattern matching, switch expressions)
- Follow Spring Boot conventions (annotations, dependency injection)
- Maintain separation of concerns (Controller → Service → Repository)
- Include validation on DTOs using Bean Validation annotations
- Write integration tests for API endpoints
- Document public APIs with OpenAPI annotations

## Critical Reminders
*   🐳 **Use `docker compose` v2** (not `docker-compose` v1).
*   ⚠️ **Trust the instruction files** linked above.
*   🧪 **Tests use Testcontainers** - no manual database setup needed.
*   📦 **Maven is the build tool** - use `mvn` commands, not Gradle.
