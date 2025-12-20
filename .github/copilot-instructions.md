# Copilot Instructions for Reflection Repository

## Repository Summary
Java Spring Boot REST API with PostgreSQL backend demonstrating modern Spring Boot patterns, REST API design, and testing with Testcontainers.

**Stack Discovery:**
```bash
# Java version
grep -E "<java.version>|<maven.compiler.source>" pom.xml

# Spring Boot version
grep -A 2 "<parent>" pom.xml | grep "<version>"

# Database
grep "image:" docker-compose.yml

# Key dependencies
grep "<artifactId>" pom.xml | grep -E "spring-boot-starter|postgresql|testcontainers"
```

## Project Purpose
This is a sample REST API project showcasing:
- RESTful API design with Spring Boot
- JPA/Hibernate entity management with PostgreSQL
- Integration testing using Testcontainers
- OpenAPI/Swagger documentation
- CI/CD with GitHub Actions
- Modern Java practices (records, pattern matching, etc.)

## Discovering Project Details

**Find Components:**
```bash
# Find all API endpoints
grep -r "@.*Mapping" src/main/java --include="*Controller.java"

# Find entities
grep -r "@Entity" src/main/java --include="*.java"

# Find DTOs
find src/main/java -name "*DTO.java" -o -name "*Dto.java"

# Find repositories
grep -r "extends JpaRepository" src/main/java --include="*.java"

# Find services
grep -r "@Service" src/main/java --include="*.java"
```

**Find Configuration:**
```bash
# Database config
grep "spring.datasource\|spring.jpa" src/main/resources/application.properties

# Server config
grep "server\." src/main/resources/application.properties

# Docker services
grep -A 10 "services:" docker-compose.yml
```

## 📚 Instruction Index
**Load these files when needed for specific tasks:**

| Task | File |
|------|------|
| **Build, Test, Run** | `.github/instructions/build-and-test.instructions.md` |
| **Code Structure** | `.github/instructions/architecture.instructions.md` |
| **Dep Research** | `.github/skills/maven-dependency-research/SKILL.md` |
| **Authoring** | `.github/instructions/copilot-authoring.instructions.md` |

## Agent Skills Available

### Maven Dependency Research
**Location:** `.github/skills/maven-dependency-research/`

**Purpose:** Research Maven dependency updates with breaking changes, security info, and migration guidance.

**Usage:**
```bash
cd .github/skills/maven-dependency-research
./scripts/fetch-maven-versions.sh <groupId> <artifactId> <currentVersion> <availableVersion>
```

See `SKILL.md` in the skill directory for complete documentation.

## Development Workflow
1. **Before Code Changes:** Always run `mvn clean compile` and `mvn test` to establish baseline
2. **Making Changes:** Follow existing patterns (discover with grep/find commands)
3. **Testing:** Write integration tests using Testcontainers (find examples in src/test/java)
4. **Validation:** Run `mvn clean package` to ensure build succeeds
5. **Manual Testing:** Use `docker compose up -d` + `mvn spring-boot:run` for local testing

## Coding Standards

**Discover Project Standards:**
```bash
# Find annotation patterns
grep -r "@SpringBootApplication\|@RestController\|@Service" src/main/java --include="*.java" | head -10

# Find validation patterns
grep -r "@Valid\|@NotNull\|@NotBlank" src/main/java --include="*.java" | head -10

# Find exception handling
grep -r "@ControllerAdvice\|@ExceptionHandler" src/main/java --include="*.java"
```

**General Guidelines:**
- Follow Spring Boot conventions (annotations, dependency injection)
- Maintain separation of concerns (Controller → Service → Repository)
- Include validation on DTOs using Bean Validation annotations
- Write integration tests for API endpoints
- Document public APIs with OpenAPI annotations
- Use Java features appropriate to project version (check pom.xml)

## Critical Reminders
*   🐳 **Use `docker compose` v2** (not `docker-compose` v1).
*   ⚠️ **Trust the instruction files** linked above.
*   🧪 **Tests use Testcontainers** - no manual database setup needed.
*   📦 **Maven is the build tool** - use `mvn` commands, not Gradle.
*   🔍 **Discover before changing** - use grep/find to understand existing patterns.
