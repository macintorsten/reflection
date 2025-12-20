---
applyTo: "**/*.java"
---

# Discovery Commands for Reflection Repository

Use these commands to quickly discover project components and patterns before making changes.

## Component Discovery

**API Endpoints:**
```bash
grep -r "@.*Mapping" src/main/java --include="*Controller.java"
```

**Entities:**
```bash
grep -r "@Entity" src/main/java --include="*.java"
```

**DTOs:**
```bash
find src/main/java -name "*DTO.java" -o -name "*Dto.java"
```

**Repositories:**
```bash
grep -r "extends JpaRepository" src/main/java --include="*.java"
```

**Services:**
```bash
grep -r "@Service" src/main/java --include="*.java"
```

## Configuration Discovery

**Database Configuration:**
```bash
grep "spring.datasource\|spring.jpa" src/main/resources/application.properties
```

**Server Configuration:**
```bash
grep "server\." src/main/resources/application.properties
```

**Docker Services:**
```bash
grep -A 10 "services:" docker-compose.yml
```

## Code Pattern Discovery

**Annotation Patterns:**
```bash
grep -r "@SpringBootApplication\|@RestController\|@Service" src/main/java --include="*.java" | head -10
```

**Validation Patterns:**
```bash
grep -r "@Valid\|@NotNull\|@NotBlank" src/main/java --include="*.java" | head -10
```

**Exception Handling:**
```bash
grep -r "@ControllerAdvice\|@ExceptionHandler" src/main/java --include="*.java"
```

## Stack Discovery

**Java Version:**
```bash
grep -E "<java.version>|<maven.compiler.source>" pom.xml
```

**Spring Boot Version:**
```bash
grep -A 2 "<parent>" pom.xml | grep "<version>"
```

**Database:**
```bash
grep "image:" docker-compose.yml
```

**Key Dependencies:**
```bash
grep "<artifactId>" pom.xml | grep -E "spring-boot-starter|postgresql|testcontainers"
```

## Usage Pattern

1. **Before making changes:** Run discovery commands to understand existing patterns
2. **Follow discovered patterns:** Maintain consistency with existing code
3. **When in doubt:** Search for similar implementations before creating new ones
