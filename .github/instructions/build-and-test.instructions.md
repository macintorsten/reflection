---
name: Build-and-Test-Instructions
description: Build, test, and validation requirements before modifying application code or configuration
applyTo: "**/*.java,**/pom.xml,**/application.properties,**/src/**"
---

# Build and Test Instructions

## When These Instructions Apply

Before making changes to:
- Java source files (`src/main/java/**`, `src/test/java/**`)
- Maven configuration (`pom.xml` - dependency versions, plugins, properties)
- Application configuration (`application.properties`, Spring config)
- Database schema or SQL files
- Any code that affects runtime behavior

**Does NOT apply to:**
- Documentation files (`*.md`, `README`, etc.)
- Analysis tools and reports
- GitHub workflows and agent definitions
- Non-executable configuration files

## Prerequisites

Java (see `pom.xml` `<source>` tag) | Maven | Docker with Compose v2 (`docker compose` NOT `docker-compose`)

Build tool versions managed by Spring Boot parent in `pom.xml`. Dev environment config in `.devcontainer/devcontainer.json`.

## Required Steps Before Code Changes

### 1. Verify Build

```bash
mvn clean compile
```

### 2. Verify Tests

```bash
mvn test
```

**Note:** Tests use Testcontainers and do NOT require `docker compose up`. Testcontainers manages its own PostgreSQL container. Database is cleaned before each test (@BeforeEach).

**Common issues:** Docker not running | Testcontainers timeout

## Build Commands Reference

```bash
mvn clean
mvn compile
mvn package
mvn clean package
```

## Running the Application

**Only for running the application (not tests):**

```bash
docker compose up -d    # Start database
mvn spring-boot:run     # Start app on :8080
```

PostgreSQL 15 on port 5432: db=mydb, user=postgres, pass=secret

Stop: `docker compose down` | Reset: `docker compose down -v && docker compose up -d`

**URLs:** `/api/samples` (API) | `/index.html` (create) | `/list.html` (view)

## Validation Steps After Changes

```bash
mvn clean compile           # 1. Compile check
mvn test                    # 2. Run tests (uses Testcontainers)
mvn clean package           # 3. Full build

# Manual API test (if needed):
docker compose up -d        # Only needed for manual testing
mvn spring-boot:run
curl -X POST http://localhost:8080/api/samples -H "Content-Type: application/json" \
  -d '{"text":"Test","number":42,"status":"active"}'
curl http://localhost:8080/api/samples
```

## Critical Reminders

⚠️ **Database for App Only:** `docker compose up` only needed for running the application, not for tests
🔧 **Two-Step Verification:** Compile → Test (Testcontainers handles test database)
🐳 **Use `docker compose` v2** NOT `docker-compose` v1