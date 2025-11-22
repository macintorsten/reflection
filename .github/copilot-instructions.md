# Copilot Instructions for Reflection Repository

## Repository Summary

Java Spring Boot REST API with PostgreSQL backend and HTML/CSS frontend. Manages "Sample" entities with CRUD operations.

**Stack:** Java (see `pom.xml`) | Maven (see `pom.xml`) | Spring Boot (see `pom.xml`) | PostgreSQL 15 | JPA/Hibernate | JUnit 5 + Testcontainers | Jackson JSON

## Build and Test Instructions

### Prerequisites

Java (see `pom.xml` `<source>` tag) | Maven | Docker with Compose v2 (`docker compose` NOT `docker-compose`)

Build tool versions managed by Spring Boot parent in `pom.xml`. Dev environment config in `.devcontainer/devcontainer.json`.

### Database Setup

**ALWAYS start PostgreSQL before builds/tests:** `docker compose up -d`

PostgreSQL 15 on port 5432: db=mydb, user=postgres, pass=secret

Stop: `docker compose down` | Reset: `docker compose down -v && docker compose up -d`

### Build Commands

```bash
mvn clean              # ~3s - removes /target/
mvn compile            # ~15-30s first run, ~5-10s cached
mvn package            # ~15-30s - runs tests, creates JAR
mvn clean package      # ~20-40s - full clean build
```

### Running Tests

**Start database first:** `docker compose up -d`

```bash
mvn test  # ~15-20s - 9 integration tests
```

Tests use Testcontainers (separate from docker-compose). Database cleaned before each test (@BeforeEach).

**Common issues:** Docker not running | Port 5432 occupied | Testcontainers timeout

### Running the Application

```bash
docker compose up -d    # Start database
mvn spring-boot:run     # Start app on :8080
```

**URLs:** `/api/samples` (API) | `/index.html` (create) | `/list.html` (view)

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

## Validation Steps

```bash
mvn clean compile           # 1. Compile check
docker compose up -d        # 2. Start DB
mvn test                    # 3. Run tests
mvn clean package           # 4. Full build

# Manual API test (if needed):
mvn spring-boot:run
curl -X POST http://localhost:8080/api/samples -H "Content-Type: application/json" \
  -d '{"text":"Test","number":42,"status":"active"}'
curl http://localhost:8080/api/samples
```

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

🔧 **Before ANY code changes:** Start DB (`docker compose up -d`) → verify `mvn clean compile` → verify `mvn test`

📦 **Timing:** Maven 15-40s for builds | Tests 15-20s (Testcontainers startup included)

🐳 **Use `docker compose` v2** NOT `docker-compose` v1
