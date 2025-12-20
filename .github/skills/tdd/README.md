# TDD (Test Driven Development) Agent Skill

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](SKILL.md)
[![Type](https://img.shields.io/badge/type-workflow-green.svg)](https://agentskills.io/what-are-skills)

Comprehensive Test Driven Development workflow automation for Java Spring Boot projects.

## Quick Start

### 1. Discover Test Patterns

```bash
cd .github/skills/tdd
./scripts/discover-test-patterns.sh
```

This analyzes your existing tests to discover:
- Test framework (JUnit 5)
- Assertion style (AssertJ, Hamcrest, JUnit)
- Mocking patterns (Mockito)
- Spring Boot test configurations
- Test naming conventions

### 2. Generate Test Scaffold

```bash
# Unit test
./scripts/generate-test-scaffold.sh UserService service unit

# Integration test
./scripts/generate-test-scaffold.sh UserController controller integration
```

Generates test files following your project's conventions.

### 3. Run Tests

```bash
# Run specific test
./scripts/run-targeted-tests.sh UserServiceTest

# Run all related tests
./scripts/run-related-tests.sh src/main/java/com/example/UserService.java

# Run full test suite
./scripts/run-full-tests.sh
```

### 4. Check Coverage

```bash
# Overall coverage
./scripts/analyze-coverage.sh

# Specific class/package
./scripts/analyze-coverage.sh com.example.service.UserService
```

## The TDD Workflow

### RED Phase
1. Write a failing test that defines desired behavior
2. Run test to verify it fails for the right reason

### GREEN Phase
1. Write minimal code to make the test pass
2. Run test to verify it passes
3. Ensure no other tests broke

### REFACTOR Phase
1. Improve code quality
2. Keep tests passing
3. Maintain or improve coverage

## Components

### Scripts (`scripts/`)
- `discover-test-patterns.sh` - Analyze existing test structure
- `generate-test-scaffold.sh` - Create test files from templates
- `run-targeted-tests.sh` - Run specific test classes
- `run-related-tests.sh` - Run tests related to changed files
- `run-full-tests.sh` - Run complete test suite
- `analyze-coverage.sh` - Generate coverage reports

### Templates (`templates/`)
- `unit-test-template.java` - Unit test scaffold with Mockito
- `integration-test-template.java` - Spring Boot integration test
- `test-report-template.md` - Comprehensive test report format

### Examples (`examples/`)
- `tdd-workflow-example.md` - Complete TDD workflow walkthrough
- `test-samples.md` - Sample test implementations

## Features

✅ **Test Discovery**
- Automatically discovers test patterns and conventions
- Identifies test framework, assertion library, and mocking style
- Finds existing test files and base classes

✅ **Scaffolding**
- Generates unit and integration tests
- Supports service, controller, repository, mapper, entity tests
- Follows project conventions automatically

✅ **Test Execution**
- Run targeted tests for rapid feedback
- Run related tests for changed files
- Full test suite execution with summary

✅ **Coverage Analysis**
- Generate JaCoCo coverage reports
- View coverage for specific classes or packages
- Track coverage over time

✅ **Best Practices**
- Given-When-Then test structure
- Descriptive test naming (should...When...)
- Proper mock usage
- Test independence and repeatability

## Supported Test Types

### Unit Tests
- Test classes in isolation
- Mock external dependencies
- Fast execution
- High coverage

### Integration Tests
- Test with Spring Boot context
- Use Testcontainers for database
- Test API endpoints with MockMvc
- Verify end-to-end flows

### Repository Tests
- Use @DataJpaTest
- Test custom queries
- Verify entity mappings

## Configuration

The skill works with standard Maven projects:
- JUnit 5 (Jupiter)
- Spring Boot Test
- Mockito
- AssertJ (recommended) or Hamcrest
- Testcontainers (for integration tests)

## Usage Examples

### Example 1: New Feature with TDD

```bash
# 1. Generate test
./scripts/generate-test-scaffold.sh EmailValidator service unit

# 2. Write failing test in EmailValidatorTest.java
# 3. Run test (RED)
./scripts/run-targeted-tests.sh EmailValidatorTest

# 4. Implement EmailValidator.java
# 5. Run test (GREEN)
./scripts/run-targeted-tests.sh EmailValidatorTest

# 6. Refactor and verify
./scripts/run-full-tests.sh
./scripts/analyze-coverage.sh com.example.EmailValidator
```

### Example 2: Test Existing Code

```bash
# 1. Discover patterns
./scripts/discover-test-patterns.sh src/main/java/com/example/LegacyService.java

# 2. Generate test
./scripts/generate-test-scaffold.sh LegacyService service unit

# 3. Write tests covering existing behavior
# 4. Verify tests pass
./scripts/run-targeted-tests.sh LegacyServiceTest

# 5. Refactor code with confidence
# 6. Ensure tests still pass
./scripts/run-targeted-tests.sh LegacyServiceTest
```

### Example 3: API Endpoint Testing

```bash
# 1. Generate integration test
./scripts/generate-test-scaffold.sh UserController controller integration

# 2. Write API tests
# 3. Run integration tests
./scripts/run-targeted-tests.sh UserControllerIntegrationTest

# 4. Check coverage
./scripts/analyze-coverage.sh com.example.controller
```

## Quality Standards

All tests should meet these criteria:

- ✅ Clear, descriptive names (should...When... pattern)
- ✅ Given-When-Then structure
- ✅ One concept per test
- ✅ Independent and repeatable
- ✅ Fast execution (<1s for unit tests)
- ✅ Meaningful assertions
- ✅ Proper mock usage
- ✅ No shared mutable state

## Coverage Goals

Recommended targets:
- **Service Layer:** 90%+ line coverage
- **Controllers:** 80%+ line coverage
- **Critical Business Logic:** 100% line coverage
- **Overall Project:** 80%+ line coverage

## Integration with CI/CD

The scripts integrate seamlessly with CI/CD pipelines:

```yaml
# GitHub Actions example
- name: Run Tests
  run: |
    cd .github/skills/tdd
    ./scripts/run-full-tests.sh

- name: Check Coverage
  run: |
    cd .github/skills/tdd
    ./scripts/analyze-coverage.sh
```

## Troubleshooting

### Tests not found
- Verify test file naming (must end with `Test.java`)
- Check test is in `src/test/java` directory
- Ensure test has `@Test` annotation

### Compilation errors
- Run `mvn clean compile` first
- Check Java version compatibility
- Verify dependencies in pom.xml

### Coverage report not generated
- Ensure JaCoCo plugin configured in pom.xml
- Run `mvn clean test` before coverage analysis
- Check `target/site/jacoco/` directory exists

## Further Reading

- **SKILL.md** - Complete skill documentation
- **examples/tdd-workflow-example.md** - Detailed TDD walkthrough
- **examples/test-samples.md** - Sample test code
- **templates/** - Test templates for reference

## Contributing

To improve this skill:
1. Add new test type templates
2. Enhance script functionality
3. Add more examples
4. Improve error handling
5. Update documentation

## Version History

- **1.0.0** (2025-12-20): Initial release
  - Test discovery and pattern analysis
  - Scaffolding for unit and integration tests
  - Test execution scripts
  - Coverage analysis
  - Comprehensive examples and documentation

## References

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Testcontainers](https://www.testcontainers.org/)
- [Agent Skills Specification](https://agentskills.io/what-are-skills)
- [TDD by Example - Kent Beck](https://www.oreilly.com/library/view/test-driven-development/0321146530/)

## License

Part of the reflection repository. See repository LICENSE for details.
