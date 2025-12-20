---
name: tdd
description: Test Driven Development (TDD) workflow automation for Java Spring Boot projects
tags: [tdd, testing, junit, testcontainers, spring-boot, test-automation]
triggers: [write tests, tdd cycle, test first, add test coverage, test-driven]
tools: [bash, grep, find, maven, junit]
prerequisites:
  - Maven build tool
  - JUnit 5 test framework
  - Project source code structure
  - Java Development Kit
version: 1.0.0
---

# Test Driven Development (TDD) Skill

A comprehensive skill for implementing Test Driven Development workflows in Java Spring Boot projects using JUnit 5, Mockito, and Testcontainers.

## Overview

This skill automates the TDD workflow by:
1. Discovering existing test patterns and conventions
2. Generating test scaffolding following project patterns
3. Running tests and providing rapid feedback
4. Analyzing test coverage and identifying gaps
5. Facilitating the Red-Green-Refactor cycle

## When to Use This Skill

Use this skill when:
- Starting new feature development with test-first approach
- Adding test coverage to existing code
- Refactoring code while maintaining test coverage
- Implementing new API endpoints with integration tests
- Following TDD best practices in Spring Boot projects

## TDD Workflow (Red-Green-Refactor)

### Phase 1: RED - Write a Failing Test

Write a test that defines the desired behavior. The test should fail initially because the implementation doesn't exist yet.

**Steps:**
1. Discover existing test patterns
2. Generate test scaffolding
3. Write specific test case
4. Run test and verify it fails for the right reason

**Scripts:**
- `scripts/discover-test-patterns.sh` - Analyze existing test structure
- `scripts/generate-test-scaffold.sh` - Create test file from template

**Example:**
```bash
# Discover patterns
./scripts/discover-test-patterns.sh src/main/java/com/example/service/UserService.java

# Generate test scaffold
./scripts/generate-test-scaffold.sh UserService service

# Run tests (should fail)
mvn test -Dtest=UserServiceTest
```

### Phase 2: GREEN - Make the Test Pass

Implement the minimum code required to make the test pass. Focus on functionality, not perfection.

**Steps:**
1. Implement the minimal code
2. Run test to verify it passes
3. Ensure no other tests broke

**Scripts:**
- `scripts/run-targeted-tests.sh` - Run specific test class
- `scripts/run-related-tests.sh` - Run tests related to changed files

**Example:**
```bash
# Run specific test
./scripts/run-targeted-tests.sh UserServiceTest

# Run all related tests
./scripts/run-related-tests.sh src/main/java/com/example/service/UserService.java
```

### Phase 3: REFACTOR - Improve the Code

Refactor the code and tests to improve design while keeping tests passing.

**Steps:**
1. Identify code smells
2. Refactor implementation
3. Run full test suite
4. Verify test coverage

**Scripts:**
- `scripts/analyze-coverage.sh` - Generate coverage report
- `scripts/run-full-tests.sh` - Run complete test suite

**Example:**
```bash
# Run full test suite
./scripts/run-full-tests.sh

# Analyze coverage
./scripts/analyze-coverage.sh com.example.service.UserService
```

## Test Types Supported

### Unit Tests

Test individual components in isolation using mocks.

**Template:** `templates/unit-test-template.java`

**Naming Convention:** `{ClassName}Test.java`

**Location:** `src/test/java/{package}/{ClassName}Test.java`

**Example:**
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository repository;
    
    @InjectMocks
    private UserService service;
    
    @Test
    void shouldReturnUserWhenFound() {
        // Given - Red phase: Define expectation
        // When - Green phase: Execute behavior
        // Then - Verify outcome
    }
}
```

### Integration Tests

Test components working together with real dependencies (using Testcontainers).

**Template:** `templates/integration-test-template.java`

**Naming Convention:** `{ClassName}IntegrationTest.java`

**Base Class:** Extend `AbstractIntegrationTest`

**Example:**
```java
@SpringBootTest
class UserControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldCreateUserSuccessfully() {
        // Integration test with real database
    }
}
```

## Scripts Reference

### discover-test-patterns.sh

Analyzes existing tests to discover project conventions.

**Parameters:**
1. `sourceFile` - Path to source file to analyze (optional)

**Output:** Test patterns, naming conventions, and common practices

**Example:**
```bash
./scripts/discover-test-patterns.sh src/main/java/com/example/service/UserService.java
```

**Discovered Information:**
- Test annotation patterns (@Test, @BeforeEach, etc.)
- Assertion library usage (AssertJ, JUnit assertions)
- Mocking framework (Mockito patterns)
- Test naming conventions
- Package structure

### generate-test-scaffold.sh

Generates test file scaffold based on source file or component type.

**Parameters:**
1. `componentName` - Name of the component (e.g., UserService)
2. `componentType` - Type: service, controller, repository, entity, mapper
3. `testType` - Optional: unit (default) or integration

**Output:** Test file scaffold in appropriate location

**Example:**
```bash
# Generate unit test
./scripts/generate-test-scaffold.sh UserService service unit

# Generate integration test
./scripts/generate-test-scaffold.sh UserController controller integration
```

### run-targeted-tests.sh

Runs specific test class or method for rapid feedback.

**Parameters:**
1. `testPattern` - Test class name or pattern

**Example:**
```bash
# Run single test class
./scripts/run-targeted-tests.sh UserServiceTest

# Run tests matching pattern
./scripts/run-targeted-tests.sh "*ServiceTest"
```

### run-related-tests.sh

Identifies and runs tests related to changed source files.

**Parameters:**
1. `sourceFile` - Path to changed source file

**Output:** Runs all tests that might be affected

**Example:**
```bash
./scripts/run-related-tests.sh src/main/java/com/example/service/UserService.java
```

**Discovery Logic:**
- Direct test for the class (UserServiceTest)
- Integration tests importing the class
- Tests in the same package
- Controller tests using the service

### analyze-coverage.sh

Generates and displays test coverage for specific classes or packages.

**Parameters:**
1. `target` - Class name or package pattern (optional, defaults to all)

**Example:**
```bash
# Coverage for specific class
./scripts/analyze-coverage.sh com.example.service.UserService

# Coverage for package
./scripts/analyze-coverage.sh com.example.service

# Full coverage report
./scripts/analyze-coverage.sh
```

**Output:** Coverage percentage and uncovered lines

### run-full-tests.sh

Runs complete test suite with formatted output.

**Example:**
```bash
./scripts/run-full-tests.sh
```

**Output:** Test results summary with pass/fail counts

## TDD Best Practices

### Test Naming

Follow the pattern: `should{ExpectedBehavior}When{Condition}`

**Examples:**
```java
@Test
void shouldReturnUserWhenValidIdProvided() { }

@Test
void shouldThrowExceptionWhenUserNotFound() { }

@Test
void shouldCreateUserSuccessfullyWithValidData() { }
```

### Test Structure (Given-When-Then)

```java
@Test
void shouldCalculateTotalPrice() {
    // Given - Setup test data and mocks
    var item = new Item("Widget", 10.00);
    var quantity = 3;
    
    // When - Execute the behavior
    var total = calculator.calculateTotal(item, quantity);
    
    // Then - Verify the outcome
    assertThat(total).isEqualTo(30.00);
}
```

### Test Independence

- Each test should be independent
- Use `@BeforeEach` for common setup
- Avoid shared mutable state
- Clean up resources in `@AfterEach`

### Mock vs Real Dependencies

**Unit Tests - Use Mocks:**
- External services
- Repositories
- Complex dependencies

**Integration Tests - Use Real:**
- Database (Testcontainers)
- Spring Context
- HTTP clients (MockMvc)

## Integration with Development Workflow

### Step 1: Start with a Story/Requirement

```bash
# Example: "As a user, I want to register with email and password"
```

### Step 2: Write Test First (RED)

```bash
# Discover patterns
./scripts/discover-test-patterns.sh

# Generate test scaffold
./scripts/generate-test-scaffold.sh UserRegistrationService service unit

# Write failing test
# Edit UserRegistrationServiceTest.java

# Run test (should fail)
./scripts/run-targeted-tests.sh UserRegistrationServiceTest
```

### Step 3: Implement Minimal Code (GREEN)

```bash
# Implement UserRegistrationService

# Run test (should pass)
./scripts/run-targeted-tests.sh UserRegistrationServiceTest
```

### Step 4: Refactor (REFACTOR)

```bash
# Refactor code and tests

# Run full test suite
./scripts/run-full-tests.sh

# Check coverage
./scripts/analyze-coverage.sh com.example.service
```

### Step 5: Repeat

Continue the cycle for each new requirement or behavior.

## Coverage Goals

**Recommended Coverage Targets:**
- **Unit Tests:** 80%+ line coverage
- **Service Layer:** 90%+ line coverage
- **Critical Business Logic:** 100% line coverage
- **Integration Tests:** All API endpoints

**Coverage Analysis:**
```bash
# Check service layer coverage
./scripts/analyze-coverage.sh com.example.service

# Check controller coverage
./scripts/analyze-coverage.sh com.example.controller
```

## Common TDD Scenarios

### Scenario 1: New REST Endpoint

```bash
# 1. Write integration test first
./scripts/generate-test-scaffold.sh UserController controller integration

# 2. Write test for POST /api/users
# Test should fail (404 Not Found)

# 3. Implement controller endpoint
# Test should pass (201 Created)

# 4. Write service unit tests
./scripts/generate-test-scaffold.sh UserService service unit

# 5. Implement service logic
# Tests should pass

# 6. Refactor and verify
./scripts/run-full-tests.sh
```

### Scenario 2: Adding Business Logic

```bash
# 1. Write unit test for business rule
./scripts/generate-test-scaffold.sh OrderValidator service unit

# 2. Write test (e.g., "order total must be > 0")
# Test fails

# 3. Implement validation logic
# Test passes

# 4. Add edge cases (negative, zero, null)
# Tests should all pass

# 5. Check coverage
./scripts/analyze-coverage.sh com.example.validation.OrderValidator
```

### Scenario 3: Refactoring Existing Code

```bash
# 1. Run existing tests (should pass)
./scripts/run-related-tests.sh src/main/java/com/example/service/LegacyService.java

# 2. Refactor code
# Tests should still pass

# 3. Add tests for uncovered scenarios
./scripts/analyze-coverage.sh com.example.service.LegacyService

# 4. Run full test suite
./scripts/run-full-tests.sh
```

## Quality Standards

All TDD workflows must include:
1. ✅ Tests written before implementation (Red phase)
2. ✅ Minimal implementation to pass tests (Green phase)
3. ✅ Refactoring with tests passing (Refactor phase)
4. ✅ Test names follow convention (should...When...)
5. ✅ Tests use Given-When-Then structure
6. ✅ Unit tests use mocks appropriately
7. ✅ Integration tests use Testcontainers
8. ✅ Tests are independent and repeatable
9. ✅ Coverage meets or exceeds targets
10. ✅ All tests pass before committing

## Boundaries

**Do:**
- Write tests before implementation
- Test one behavior per test method
- Use descriptive test names
- Follow project conventions
- Keep tests fast and focused
- Mock external dependencies in unit tests

**Don't:**
- Skip the Red phase (always verify test fails first)
- Test implementation details (test behavior)
- Create interdependent tests
- Mix unit and integration test concerns
- Over-mock in integration tests
- Commit code with failing tests

## Error Handling

Scripts include proper error handling:
- Clear error messages for missing files
- Validation of prerequisites (Maven, Java)
- Graceful handling of test failures
- Helpful suggestions for fixing issues

## Examples

See `examples/` directory for:
- `tdd-workflow-example.md` - Complete TDD workflow walkthrough
- `test-samples.md` - Sample test implementations
- `coverage-report-example.md` - Sample coverage analysis

## Tool Integration

### Maven

```bash
# Run tests
mvn test

# Run specific test
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report

# Skip tests (not recommended in TDD!)
mvn clean package -DskipTests
```

### IDE Integration

Most IDEs support running tests directly:
- IntelliJ IDEA: Right-click test class → Run
- VS Code: Use Test Explorer
- Eclipse: Right-click → Run As → JUnit Test

### CI/CD Integration

```bash
# In GitHub Actions workflow
- name: Run Tests
  run: mvn test

- name: Generate Coverage
  run: mvn jacoco:report
```

## Maintenance

**Version History:**
- 1.0.0 (2025-12-20): Initial release

**Future Enhancements:**
- Mutation testing support
- Property-based testing examples
- Performance test templates
- Contract testing support
- Test data generation utilities

## References

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Testcontainers Documentation](https://www.testcontainers.org/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [TDD by Example - Kent Beck](https://www.oreilly.com/library/view/test-driven-development/0321146530/)
- [Agent Skills Specification](https://agentskills.io/what-are-skills)
