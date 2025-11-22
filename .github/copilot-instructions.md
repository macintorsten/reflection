# Copilot Instructions for Reflection Repository

## Repository Summary

Java Spring Boot REST API with PostgreSQL backend and HTML/CSS frontend. Manages "Sample" entities with CRUD operations.

**Stack:** Java (see `pom.xml`) | Maven (see `pom.xml`) | Spring Boot (see `pom.xml`) | PostgreSQL 15 | JPA/Hibernate | JUnit 5 + Testcontainers | Jackson JSON

## Quick Reference

**Build & Test:** See [Build and Test Instructions](instructions/build-and-test.instructions.md) for detailed workflow  
**Database:** PostgreSQL 15 at `localhost:5432/mydb` (user: postgres, pass: secret)  
**Start App:** `docker compose up -d && mvn spring-boot:run` → http://localhost:8080

## Project Layout and Architecture

### Directory Structure

Main source code in `src/main/java/com/example/reflection/`:
- `ReflectionApplication.java` - Spring Boot main class
- `Sample.java` - JPA Entity with Jackson annotations
- `SampleDTO.java` - Data Transfer Object with validation
- `SampleController.java` - REST Controller
- `SampleRepository.java` - Spring Data JPA Repository

Resources in `src/main/resources/`:
- `application.properties` - Database config
- `static/` - HTML/CSS frontend (index.html, list.html, styles.css)

Tests in `src/test/java/com/example/reflection/`:
- `SampleControllerIntegrationTest.java` - Integration tests (Testcontainers)

Config files in root:
- `pom.xml` - Maven/Spring Boot config
- `docker-compose.yml` - PostgreSQL service
- `.devcontainer/devcontainer.json` - Dev environment

### Key Files

**Config:** `pom.xml` (Maven/Spring Boot) | `application.properties` (DB config) | `docker-compose.yml` (PostgreSQL)

**Core:** `ReflectionApplication.java` (main) | `Sample.java` (JPA entity) | `SampleDTO.java` (validation) | `SampleController.java` (REST) | `SampleRepository.java` (JPA repo)

**Test:** `SampleControllerIntegrationTest.java` (integration tests with Testcontainers)

### Architecture

- Annotation-based config: `@SpringBootApplication`, `@RestController`, `@Repository`
- Auto-configured JPA/Hibernate, Jackson JSON, schema generation (`ddl-auto=update`)
- `Sample` entity: nested classes, `@Transient` fields (not persisted), enum with custom JSON
- API: POST/GET `/api/samples` | Validation via `@Valid` | Public fields (no getters/setters)
- DB: PostgreSQL 15 at `localhost:5432/mydb` | Tests use Testcontainers

## Dependencies

See `pom.xml` for current versions. Key dependencies:
- Spring Boot parent (manages most dependency versions)
- Spring Boot starters: web, data-jpa, validation, test
- PostgreSQL driver
- Testcontainers (core, postgresql, junit-jupiter)
- Guava, Commons Lang3, HttpClient5

## Git Ignore

Excludes: `/target/`, `*.class`, `*.jar`, IDE files (`.idea/`, `.vscode/`, etc.), OS files, Maven wrapper

## Critical Reminders

⚠️ **Trust these instructions first** - only search if instructions are incomplete/incorrect

🔧 **Before code/config changes:** See [Build and Test Instructions](instructions/build-and-test.instructions.md) for verification steps

🐳 **Use `docker compose` v2** NOT `docker-compose` v1
