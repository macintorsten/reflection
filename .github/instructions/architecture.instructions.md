---
name: Architecture-Instructions
description: Project layout, key files, and architectural patterns
applyTo: "**/*.java"
---

# Project Layout and Architecture

## Directory Structure

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

## Key Files

**Config:** `pom.xml` (Maven/Spring Boot) | `application.properties` (DB config) | `docker-compose.yml` (PostgreSQL)

**Core:** `ReflectionApplication.java` (main) | `Sample.java` (JPA entity) | `SampleDTO.java` (validation) | `SampleController.java` (REST) | `SampleRepository.java` (JPA repo)

**Test:** `SampleControllerIntegrationTest.java` (integration tests with Testcontainers)

## Architecture

- Annotation-based config: `@SpringBootApplication`, `@RestController`, `@Repository`
- Auto-configured JPA/Hibernate, Jackson JSON, schema generation (`ddl-auto=update`)
- `Sample` entity: nested classes, `@Transient` fields (not persisted), enum with custom JSON
- API: POST/GET `/api/samples` | Validation via `@Valid` | Public fields (no getters/setters)
- DB: PostgreSQL 15 at `localhost:5432/mydb` | Tests use Testcontainers
