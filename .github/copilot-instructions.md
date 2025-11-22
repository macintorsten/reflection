# Copilot Instructions for Reflection Repository

## Repository Summary

Java Spring Boot REST API with PostgreSQL backend and HTML/CSS frontend. Manages "Sample" entities with CRUD operations.

**Stack:** Java 17 | Maven 3.9.11 | Spring Boot 3.2.1 | PostgreSQL 15 | JPA/Hibernate | JUnit 5 + Testcontainers | Jackson JSON

**Size:** ~10 Java files, ~2,700 lines total

## Build and Test Instructions

### Prerequisites

Java 17 | Maven 3.9.11+ | Docker with Compose v2 (`docker compose` NOT `docker-compose`)

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

```
/home/runner/work/reflection/reflection/
├── .devcontainer/
│   └── devcontainer.json          # VS Code dev container config (Java 21, Maven)
├── .github/
│   ├── agents/                     # Custom agent definitions
│   └── instructions/               # Agent instruction templates
├── src/
│   ├── main/
│   │   ├── java/com/example/reflection/
│   │   │   ├── ReflectionApplication.java    # Spring Boot main class (@SpringBootApplication)
│   │   │   ├── Sample.java                   # JPA Entity with Jackson annotations
│   │   │   ├── SampleDTO.java                # Data Transfer Object with validation
│   │   │   ├── SampleController.java         # REST Controller (@RestController)
│   │   │   └── SampleRepository.java         # Spring Data JPA Repository
│   │   └── resources/
│   │       ├── application.properties        # Database config (PostgreSQL connection)
│   │       └── static/                       # Static web resources
│   │           ├── index.html                # Create sample form
│   │           ├── list.html                 # List samples page
│   │           └── styles.css                # Shared CSS styles
│   └── test/
│       └── java/com/example/reflection/
│           └── SampleControllerIntegrationTest.java  # Integration tests (10 tests)
├── .gitignore                      # Java/Maven/IDE ignores
├── docker-compose.yml              # PostgreSQL 15 database service
└── pom.xml                         # Maven configuration (Spring Boot 3.2.1)
```

### Key Files

**Config:** `pom.xml` (Spring Boot 3.2.1) | `application.properties` (DB config) | `docker-compose.yml` (PostgreSQL)

**Core:** `ReflectionApplication.java` (main) | `Sample.java` (JPA entity) | `SampleDTO.java` (validation) | `SampleController.java` (REST) | `SampleRepository.java` (JPA repo)

**Test:** `SampleControllerIntegrationTest.java` (9 tests, Testcontainers)

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

Java 17 | Maven 3.9.11 | Spring Boot 3.2.1 | Spring 6.2.1 | JUnit 5.10.1 | Jackson 2.15.3 | PostgreSQL 42.7.1 | Testcontainers 1.19.3 | Guava 33.0.0 | Commons Lang3 3.14.0

## Git Ignore

Excludes: `/target/`, `*.class`, `*.jar`, IDE files (`.idea/`, `.vscode/`, etc.), OS files, Maven wrapper

## Critical Reminders

⚠️ **Trust these instructions first** - only search if instructions are incomplete/incorrect

🔧 **Before ANY code changes:** Start DB (`docker compose up -d`) → verify `mvn clean compile` → verify `mvn test`

📦 **Timing:** Maven 15-40s for builds | Tests 15-20s (Testcontainers startup included)

🐳 **Use `docker compose` v2** NOT `docker-compose` v1
