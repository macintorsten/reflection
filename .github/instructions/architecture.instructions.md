---
name: Architecture-Instructions
description: Project layout, key files, and architectural patterns - use discovery commands to find components
applyTo: "**/*.java"
---

# Project Layout and Architecture

## How to Discover Project Structure

**Find Main Application Class:**
```bash
grep -r "@SpringBootApplication" src/main/java --include="*.java"
```

**Find Entities:**
```bash
grep -r "@Entity" src/main/java --include="*.java"
```

**Find DTOs:**
```bash
find src/main/java -name "*DTO.java" -o -name "*Dto.java"
```

**Find Controllers:**
```bash
grep -r "@RestController\|@Controller" src/main/java --include="*.java"
```

**Find Repositories:**
```bash
grep -r "@Repository\|extends JpaRepository\|extends CrudRepository" src/main/java --include="*.java"
```

**Find Services:**
```bash
grep -r "@Service" src/main/java --include="*.java"
```

**Find Configuration:**
```bash
find src/main/resources -name "application*.properties" -o -name "application*.yml"
```

**Find Tests:**
```bash
find src/test/java -name "*Test.java"
grep -r "@SpringBootTest\|@WebMvcTest\|@DataJpaTest" src/test/java --include="*.java"
```

## Directory Structure Pattern

Typical Spring Boot project layout:
```
src/main/java/{base-package}/     # Main application code
  {Application}.java               # @SpringBootApplication main class
  {Entity}.java                    # @Entity domain models
  {Entity}DTO.java                 # Data Transfer Objects
  {Entity}Controller.java          # @RestController REST endpoints
  {Entity}Repository.java          # @Repository data access
  {Entity}Service.java             # @Service business logic
  
src/main/resources/
  application.properties           # Configuration
  static/                          # Static web resources
  
src/test/java/{base-package}/     # Tests
  {Component}Test.java             # Unit/Integration tests
  
pom.xml                            # Maven configuration
docker-compose.yml                 # Database services (if exists)
```

## Discovering Configuration

**Database Configuration:**
```bash
# From application.properties
grep "spring.datasource" src/main/resources/application.properties

# From docker-compose.yml
grep -A 10 "services:" docker-compose.yml | grep -E "image:|POSTGRES_"
```

**API Endpoints:**
```bash
# Find all controller mappings
grep -r "@.*Mapping" src/main/java --include="*Controller.java"

# Find base paths
grep -r "@RequestMapping" src/main/java --include="*Controller.java"
```

**Dependencies:**
```bash
# List all dependencies
grep -A 2 "<dependency>" pom.xml | grep "<artifactId>"

# Find specific frameworks
grep "spring-boot-starter" pom.xml
```

## Discovering Architectural Patterns

**Annotation-Based Configuration:**
```bash
# Find configuration classes
grep -r "@Configuration\|@EnableWebMvc\|@EnableJpaRepositories" src/main/java --include="*.java"
```

**Validation:**
```bash
# Find validation annotations
grep -r "@Valid\|@NotNull\|@NotBlank\|@Size" src/main/java --include="*.java"
```

**Exception Handling:**
```bash
# Find exception handlers
grep -r "@ControllerAdvice\|@ExceptionHandler" src/main/java --include="*.java"
```

**API Documentation:**
```bash
# Find OpenAPI/Swagger dependencies
grep -i "springdoc\|swagger" pom.xml

# Find API documentation annotations
grep -r "@Tag\|@Operation\|@ApiResponse" src/main/java --include="*.java"
```

## Discovering Code Conventions

**Examine Existing Code:**
```bash
# Look at entity patterns
find src/main/java -name "*Entity*.java" -o -name "*Model*.java" | head -3 | xargs cat

# Look at controller patterns  
find src/main/java -name "*Controller.java" | head -1 | xargs cat

# Look at test patterns
find src/test/java -name "*Test.java" | head -1 | xargs cat
```

**Common Patterns to Look For:**
- Field access (public fields vs getters/setters)
- Validation approach (Bean Validation annotations)
- DTO mapping patterns (manual vs MapStruct)
- Exception handling strategy
- Response wrapping patterns
- Logging conventions

## Testing Strategy Discovery

**Find Test Dependencies:**
```bash
grep "<scope>test</scope>" pom.xml -B 3 | grep "<artifactId>"
```

**Identify Test Types:**
```bash
# Integration tests
grep -r "@SpringBootTest" src/test/java --include="*.java"

# Testcontainers usage
grep -r "Testcontainers\|@Container" src/test/java --include="*.java"

# Web tests
grep -r "@WebMvcTest\|MockMvc" src/test/java --include="*.java"
```

## Architecture Guidelines

When making changes:
1. **Discover before modifying**: Use grep/find to understand existing patterns
2. **Follow existing conventions**: Match code style from existing components
3. **Maintain separation of concerns**: Keep Controller → Service → Repository layers
4. **Match validation patterns**: Use same validation approach as existing code
5. **Follow test patterns**: Write tests similar to existing test classes
6. **Respect package structure**: Keep related components in same package
