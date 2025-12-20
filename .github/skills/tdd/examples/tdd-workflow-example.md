# TDD Workflow Example - Adding a New Feature

This example demonstrates a complete TDD workflow for adding a new feature to the application: **Email validation for user registration**.

## Feature Requirement

**User Story:** As a user, I want my email address to be validated during registration so that I can't register with an invalid email.

**Acceptance Criteria:**
1. Valid email formats should be accepted
2. Invalid email formats should be rejected
3. Null or empty emails should be rejected
4. Duplicate emails should be rejected

---

## Phase 1: RED - Write Failing Tests

### Step 1: Discover Existing Test Patterns

```bash
cd .github/skills/tdd
./scripts/discover-test-patterns.sh
```

**Output:**
```
=== Test Pattern Discovery ===

## Test Framework
Framework: JUnit 5 (Jupiter)

## Assertion Library
Assertions: AssertJ (assertThat)

## Mocking Framework
Mocking: Mockito (annotations)

## Spring Boot Testing
Integration Tests: @SpringBootTest
Database Tests: Testcontainers

## Test Naming Conventions
Sample test methods:
  - shouldReturnAllSamplesWhenRequested()
  - shouldCreateSampleSuccessfully()
  - shouldThrowExceptionWhenInvalidData()
```

### Step 2: Generate Test Scaffold

```bash
./scripts/generate-test-scaffold.sh EmailValidator service unit
```

**Output:**
```
Found source file: src/main/java/com/example/service/EmailValidator.java
Package: com.example.service

✓ Generated test file: src/test/java/com/example/service/EmailValidatorTest.java

Next steps:
  1. Edit src/test/java/com/example/service/EmailValidatorTest.java
  2. Replace TODO items with actual dependencies
  3. Write failing test (RED phase)
  4. Run test: mvn test -Dtest=EmailValidatorTest
```

### Step 3: Write First Failing Test

Edit `EmailValidatorTest.java`:

```java
package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EmailValidatorTest {

    private EmailValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EmailValidator();
    }

    @Test
    void shouldReturnTrueWhenEmailIsValid() {
        // Given - Valid email
        String validEmail = "user@example.com";
        
        // When - Validate email
        boolean result = validator.isValid(validEmail);
        
        // Then - Should be valid
        assertThat(result).isTrue();
    }
}
```

### Step 4: Run Test (Should Fail - RED)

```bash
./scripts/run-targeted-tests.sh EmailValidatorTest
```

**Expected Output:**
```
=== Running Targeted Tests ===
Pattern: EmailValidatorTest

[ERROR] Compilation failure
EmailValidator.java:[10,8] cannot find symbol
  symbol:   method isValid(java.lang.String)
  location: class EmailValidator

✗ Tests failed
```

**Perfect!** The test fails because `isValid()` method doesn't exist yet. This is the RED phase. ✅

---

## Phase 2: GREEN - Make Test Pass

### Step 5: Implement Minimal Code

Create/Edit `EmailValidator.java`:

```java
package com.example.service;

public class EmailValidator {
    
    public boolean isValid(String email) {
        // Minimal implementation to make test pass
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.contains("@") && email.contains(".");
    }
}
```

### Step 6: Run Test (Should Pass - GREEN)

```bash
./scripts/run-targeted-tests.sh EmailValidatorTest
```

**Expected Output:**
```
=== Running Targeted Tests ===
Pattern: EmailValidatorTest

[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

✓ Tests passed
```

**Excellent!** Test passes with minimal implementation. This is the GREEN phase. ✅

---

## Continuing TDD Cycle

### Step 7: Add More Tests (RED)

Add test for invalid email:

```java
@Test
void shouldReturnFalseWhenEmailIsInvalid() {
    // Given - Invalid email (no @ symbol)
    String invalidEmail = "notanemail";
    
    // When - Validate email
    boolean result = validator.isValid(invalidEmail);
    
    // Then - Should be invalid
    assertThat(result).isFalse();
}

@Test
void shouldReturnFalseWhenEmailIsNull() {
    // Given - Null email
    String nullEmail = null;
    
    // When - Validate email
    boolean result = validator.isValid(nullEmail);
    
    // Then - Should be invalid
    assertThat(result).isFalse();
}

@Test
void shouldReturnFalseWhenEmailIsEmpty() {
    // Given - Empty email
    String emptyEmail = "";
    
    // When - Validate email
    boolean result = validator.isValid(emptyEmail);
    
    // Then - Should be invalid
    assertThat(result).isFalse();
}
```

### Step 8: Run Tests (Should All Pass - GREEN)

```bash
./scripts/run-targeted-tests.sh EmailValidatorTest
```

**Output:**
```
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0

✓ Tests passed
```

All tests pass! ✅

---

## Phase 3: REFACTOR - Improve Code Quality

### Step 9: Refactor Implementation

The current implementation is too simple. Let's improve it with proper email validation:

```java
package com.example.service;

import java.util.regex.Pattern;

public class EmailValidator {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    public boolean isValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
```

### Step 10: Run Tests After Refactor

```bash
./scripts/run-targeted-tests.sh EmailValidatorTest
```

**Output:**
```
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0

✓ Tests passed
```

Great! Tests still pass after refactoring. ✅

### Step 11: Add Edge Cases

```java
@Test
void shouldReturnFalseWhenEmailHasMultipleAtSymbols() {
    // Given
    String invalidEmail = "user@@example.com";
    
    // When
    boolean result = validator.isValid(invalidEmail);
    
    // Then
    assertThat(result).isFalse();
}

@Test
void shouldReturnTrueWhenEmailHasSubdomain() {
    // Given
    String validEmail = "user@mail.example.com";
    
    // When
    boolean result = validator.isValid(validEmail);
    
    // Then
    assertThat(result).isTrue();
}

@Test
void shouldReturnFalseWhenEmailMissingDomain() {
    // Given
    String invalidEmail = "user@";
    
    // When
    boolean result = validator.isValid(invalidEmail);
    
    // Then
    assertThat(result).isFalse();
}
```

### Step 12: Verify All Tests Pass

```bash
./scripts/run-full-tests.sh
```

---

## Integration Test

### Step 13: Create Integration Test

```bash
./scripts/generate-test-scaffold.sh UserRegistrationController controller integration
```

Edit the generated integration test:

```java
@Test
void shouldRejectRegistrationWhenEmailInvalid() throws Exception {
    // Given - Registration request with invalid email
    String requestBody = """
        {
            "email": "notanemail",
            "password": "SecurePass123!"
        }
        """;
    
    // When & Then - Should return bad request
    mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid email format"));
}

@Test
void shouldAcceptRegistrationWhenEmailValid() throws Exception {
    // Given - Registration request with valid email
    String requestBody = """
        {
            "email": "user@example.com",
            "password": "SecurePass123!"
        }
        """;
    
    // When & Then - Should create user
    mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("user@example.com"));
}
```

### Step 14: Run Integration Tests

```bash
./scripts/run-targeted-tests.sh UserRegistrationControllerIntegrationTest
```

---

## Coverage Analysis

### Step 15: Check Test Coverage

```bash
./scripts/analyze-coverage.sh com.example.service.EmailValidator
```

**Output:**
```
=== Analyzing Test Coverage ===

Running tests with coverage...

✓ Coverage report generated
Report location: target/site/jacoco/index.html

Overall Coverage: 94%

Coverage for: com.example.service.EmailValidator
  Line Coverage: 100%
  Branch Coverage: 92%
```

Excellent coverage! ✅

---

## Final Verification

### Step 16: Run Full Test Suite

```bash
./scripts/run-full-tests.sh
```

**Output:**
```
=== Running Full Test Suite ===

[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0

=== Test Summary ===
Total Tests: 25
Passed: 25
Failed: 0
Errors: 0
Skipped: 0

✓ All tests passed
```

---

## Summary

This TDD workflow demonstrated:

### RED Phase ✅
- Wrote tests first
- Verified tests failed for the right reason
- Test names followed convention (should...When...)

### GREEN Phase ✅
- Implemented minimal code to pass tests
- Tests passed without over-engineering
- Incremental implementation

### REFACTOR Phase ✅
- Improved implementation quality
- Tests still passed after refactoring
- Added edge case coverage

### Results
- **7 unit tests** covering EmailValidator
- **2 integration tests** for user registration
- **100% line coverage** for EmailValidator
- **92% branch coverage** for EmailValidator
- **All tests passing** ✅

---

## TDD Cycle Metrics

**Time Breakdown:**
- Test Discovery: 2 minutes
- Writing Tests: 10 minutes
- Implementation: 8 minutes
- Refactoring: 5 minutes
- **Total:** 25 minutes

**Test Execution:**
- Unit Tests: 0.3 seconds
- Integration Tests: 2.1 seconds
- **Total:** 2.4 seconds

**Code Changes:**
- Lines Added: 45 (35 test, 10 production)
- Test-to-Code Ratio: 3.5:1 ✅

---

## Lessons Learned

1. **Start Simple:** Initial implementation was simple (just checking for @ and .)
2. **Refactor with Confidence:** Tests ensured refactoring didn't break functionality
3. **Edge Cases Matter:** Comprehensive tests caught edge cases
4. **Fast Feedback:** Targeted test runs provided immediate feedback
5. **Integration Validates:** Integration tests confirmed feature works end-to-end

---

## Next Feature

Continue TDD cycle for next requirement: "Password strength validation"

```bash
# Generate test scaffold
./scripts/generate-test-scaffold.sh PasswordValidator service unit

# Start RED phase - write failing test
# ... continue TDD cycle
```
