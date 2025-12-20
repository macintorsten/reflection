# Copilot Instructions for Reflection Repository

## What This Repository Does

Java 21 Spring Boot REST API with PostgreSQL demonstrating REST API design, JPA/Hibernate, integration testing with Testcontainers, OpenAPI documentation, and CI/CD with GitHub Actions.

## Technology Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.5.8
- **Database:** PostgreSQL 17 (via Docker Compose)
- **Build Tool:** Maven 3.9.9
- **Testing:** JUnit 5, Testcontainers, Playwright Java
- **Frontend:** Vanilla HTML/CSS/JavaScript

## Build, Test, and Run

**Build the project:**
```bash
mvn clean package
```

**Run all tests:**
```bash
mvn test
```

**Start application locally:**
```bash
# Start PostgreSQL
docker compose up -d

# Run application
mvn spring-boot:run

# Access at http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

**Run specific test types:**
```bash
# Backend tests only (excludes frontend)
mvn test -Dtest="!*FrontendTest"

# Frontend tests only (Playwright)
mvn test -Dtest="*FrontendTest"

# Debug frontend tests with visible browser
HEADED=true mvn test -Dtest="V1DashboardFrontendTest"
```

## Architecture

**Layered:** Controller → Service → Repository  
**Versioning:** v1/v2 packages (URL-based)  
**Testing:** Testcontainers (PostgreSQL), Playwright (frontend E2E)

## Validation Before Committing

**Always run these commands to verify changes:**
```bash
# 1. Compile check
mvn clean compile

# 2. Run all tests
mvn test

# 3. Full package (includes integration tests)
mvn clean package
```

**CI/CD Pipeline:** GitHub Actions runs `mvn clean verify` on push/PR.

## Path-Specific Instructions

**Detailed instructions are in `.github/instructions/`:**
- `build-and-test.instructions.md` - Required before modifying application code
- `architecture.instructions.md` - Applies to `**/*.java`
- `api-versioning.instructions.md` - API versioning rules
- `development-workflow.instructions.md` - Applies to all files
- `discovery.instructions.md` - Commands to find components (applies to `**/*.java`)
- `copilot-authoring.instructions.md` - Applies to `.github/**/*.md`

**Agent Skills (see SKILL.md in each directory):**
- `.github/skills/maven-dependency-research/` - Research dependency updates
- `.github/skills/tdd/` - Test-driven development automation
- `.github/skills/playwright-frontend-testing/` - Frontend E2E testing

## Critical Rules

⚠️ **Trust the instruction files** - Load them before making changes to their applicable paths.

🐳 **Use `docker compose`** (v2), not `docker-compose` (v1).

🧪 **Tests use Testcontainers** - No manual database setup required.

📦 **Maven is the build tool** - Use `mvn` commands, not Gradle.

✅ **Always validate before committing** - Run `mvn clean package` to catch issues early.
