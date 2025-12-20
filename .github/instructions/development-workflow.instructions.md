---
applyTo: "**"
---

# Development Workflow

Standard workflow for making changes to the Reflection repository.

## Before Code Changes

**Always establish baseline:**
```bash
# Compile to verify clean starting state
mvn clean compile

# Run tests to verify passing state
mvn test
```

## Making Changes

1. **Discover existing patterns** using discovery commands (see discovery.instructions.md)
2. **Follow Spring Boot conventions:**
   - Use dependency injection
   - Maintain layer separation (Controller → Service → Repository)
   - Apply Bean Validation annotations on DTOs
   - Document APIs with OpenAPI annotations
3. **Write tests first** using TDD skill when appropriate
4. **Test locally:**
   ```bash
   # Start dependencies
   docker compose up -d
   
   # Run application
   mvn spring-boot:run
   ```

## Validation Before Commit

**Required validation steps:**
```bash
# Full build with tests
mvn clean package

# Verify no compilation errors
mvn clean compile

# Run all tests
mvn verify
```

**For backend changes:**
```bash
# Run backend tests only
mvn test -Dtest="!*FrontendTest"
```

**For frontend changes:**
```bash
# Run frontend tests
mvn test -Dtest="*FrontendTest"

# Debug with visible browser
HEADED=true mvn test -Dtest="V1DashboardFrontendTest"
```

## Integration Testing

- All API endpoints require integration tests using Testcontainers
- Find test examples in `src/test/java` matching the component you're testing
- Tests automatically start PostgreSQL container - no manual database setup needed

## Local Development

**Start PostgreSQL:**
```bash
docker compose up -d
```

**Run application:**
```bash
mvn spring-boot:run
```

**Access application:**
- API: http://localhost:8080
- OpenAPI docs: http://localhost:8080/swagger-ui.html
- V1 UI: http://localhost:8080/v1-ui.html
- V2 UI: http://localhost:8080/v2-ui.html

## Coding Standards

- Follow Spring Boot conventions and annotations
- Maintain separation of concerns
- Use Bean Validation annotations (@Valid, @NotNull, @NotBlank)
- Write integration tests for API endpoints
- Use Java features appropriate to Java 21 (records, pattern matching, etc.)

## Critical Reminders

- 🐳 Use `docker compose` (v2), not `docker-compose` (v1)
- 🧪 Tests use Testcontainers - no manual database setup
- 📦 Maven is the build tool - use `mvn` commands
- 🔍 Discover patterns before creating new code
- ⚠️ Always validate changes with `mvn clean package`
