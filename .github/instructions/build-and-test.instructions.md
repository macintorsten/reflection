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

### 1. Start Database

**ALWAYS start PostgreSQL before builds/tests:** `docker compose up -d`

PostgreSQL 15 on port 5432: db=mydb, user=postgres, pass=secret

Stop: `docker compose down` | Reset: `docker compose down -v && docker compose up -d`

### 2. Verify Build

```bash
mvn clean compile      # ~15-30s first run, ~5-10s cached
```

### 3. Verify Tests

```bash
mvn test              # ~15-20s - 9 integration tests
```

Tests use Testcontainers (separate from docker-compose). Database cleaned before each test (@BeforeEach).

**Common issues:** Docker not running | Port 5432 occupied | Testcontainers timeout

## Build Commands Reference

```bash
mvn clean              # ~3s - removes /target/
mvn compile            # ~15-30s first run, ~5-10s cached
mvn package            # ~15-30s - runs tests, creates JAR
mvn clean package      # ~20-40s - full clean build
```

## Running the Application

```bash
docker compose up -d    # Start database
mvn spring-boot:run     # Start app on :8080
```

**URLs:** `/api/samples` (API) | `/index.html` (create) | `/list.html` (view)

## Validation Steps After Changes

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

## Critical Reminders

⚠️ **Database Required:** Application and tests need PostgreSQL running

🔧 **Three-Step Verification:** Start DB → Compile → Test

📦 **Timing:** Maven 15-40s for builds | Tests 15-20s (Testcontainers startup included)

🐳 **Use `docker compose` v2** NOT `docker-compose` v1
