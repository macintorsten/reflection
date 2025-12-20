---
name: tdd
description: Test Driven Development (TDD) workflow automation for Java Spring Boot projects. Use for writing tests, checking coverage, generating test scaffolds, and implementing Red-Green-Refactor cycles with JUnit 5, Mockito, and Testcontainers.
---

# TDD Skill

Automates Test Driven Development workflows for Java Spring Boot projects.

## Quick Start: Coverage Analysis

**Generate and check coverage (no scripts needed):**

```bash
# 1. Run tests with coverage
mvn test jacoco:report -q

# 2. Overall coverage
awk -F',' 'NR>1{ic+=$5;im+=$4}END{printf "%.1f%%\n",ic*100/(ic+im)}' target/site/jacoco/jacoco.csv

# 3. Classes below 95%
awk -F',' 'NR>1 && $4+$5>0{pct=$5*100/($4+$5);if(pct<95)printf "%.1f%% %s.%s (%d lines)\n",pct,$2,$3,$8}' target/site/jacoco/jacoco.csv | sort -n

# 4. Specific class
grep "ClassName" target/site/jacoco/jacoco.csv | awk -F',' '{printf "%.1f%%\n",$5*100/($4+$5)}'
```

**CSV columns:** `INSTRUCTION_MISSED($4), INSTRUCTION_COVERED($5), LINE_MISSED($8), LINE_COVERED($9)`

## Red-Green-Refactor Cycle

### 1. RED - Write Failing Test

```bash
# Generate test scaffold
./scripts/generate-test-scaffold.sh UserService service

# Write test, run to verify it fails
mvn test -Dtest=UserServiceTest
```

### 2. GREEN - Make Test Pass

```bash
# Implement minimal code to pass

# Run test to verify
mvn test -Dtest=UserServiceTest
```

### 3. REFACTOR - Improve Code

```bash
# Refactor while keeping tests green

# Verify all tests pass
mvn test

# Check coverage
mvn test jacoco:report -q && \
awk -F',' 'NR>1{ic+=$5;im+=$4}END{printf "Coverage: %.1f%%\n",ic*100/(ic+im)}' target/site/jacoco/jacoco.csv
```

## Test Types

**Unit Test:**
- Location: `src/test/java/{package}/{ClassName}Test.java`
- Uses: `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`
- Tests: Single component in isolation

**Integration Test:**
- Location: `src/test/java/{package}/{ClassName}IntegrationTest.java`
- Extends: `AbstractIntegrationTest`
- Uses: `@SpringBootTest`, Testcontainers for real DB
- Tests: Components working together

## Test Naming Convention

```java
@Test
void shouldReturnUserWhenValidIdProvided() { }

@Test
void shouldThrowExceptionWhenUserNotFound() { }
```

Pattern: `should{ExpectedBehavior}When{Condition}`

## Test Structure (Given-When-Then)

```java
@Test
void shouldCalculateTotalPrice() {
    // Given - Setup test data
    var item = new Item("Widget", 10.00);
    var quantity = 3;
    
    // When - Execute behavior
    var total = calculator.calculateTotal(item, quantity);
    
    // Then - Verify outcome
    assertThat(total).isEqualTo(30.00);
}
```

## Coverage Targets

- **Service Layer:** 90%+
- **Critical Logic:** 100%
- **Integration Tests:** All API endpoints

**Layer-specific coverage:**
```bash
# Services
grep "\.service\." target/site/jacoco/jacoco.csv | awk -F',' '{ic+=$5;im+=$4}END{printf "%.1f%%\n",ic*100/(ic+im)}'

# Controllers
grep "\.controller\." target/site/jacoco/jacoco.csv | awk -F',' '{ic+=$5;im+=$4}END{printf "%.1f%%\n",ic*100/(ic+im)}'
```

## Scripts Available

**Optional - use direct commands above for simpler workflow**

- `scripts/discover-test-patterns.sh` - Analyze existing test patterns
- `scripts/generate-test-scaffold.sh <ComponentName> <type> [unit|integration]` - Create test file
- `scripts/run-targeted-tests.sh <TestPattern>` - Run specific tests
- `scripts/analyze-coverage.sh [target]` - Coverage report (wraps JaCoCo)

## Common Workflows

**New REST Endpoint:**
```bash
# 1. Write integration test (should fail - 404)
./scripts/generate-test-scaffold.sh UserController controller integration

# 2. Implement endpoint (test passes - 201)
# 3. Write service unit tests
# 4. Verify coverage
mvn test jacoco:report -q && grep "UserService" target/site/jacoco/jacoco.csv
```

**Add Business Logic:**
```bash
# 1. Write unit test for rule (fails)
# 2. Implement logic (passes)
# 3. Add edge cases
# 4. Check coverage
```

## Best Practices

✅ Write tests before implementation (Red phase)
✅ Test one behavior per test method
✅ Use descriptive test names
✅ Keep tests fast and focused
✅ Mock external dependencies in unit tests
✅ Use real DB (Testcontainers) in integration tests

❌ Don't skip Red phase (verify test fails first)
❌ Don't test implementation details (test behavior)
❌ Don't create interdependent tests
❌ Don't commit with failing tests

## Complex Analysis (Python)

When awk isn't sufficient:

```bash
python3 << 'EOF'
import csv
with open('target/site/jacoco/jacoco.csv') as f:
    rows = list(csv.DictReader(f))
    covered = sum(int(r['INSTRUCTION_COVERED']) for r in rows)
    missed = sum(int(r['INSTRUCTION_MISSED']) for r in rows)
    total = covered + missed
    print(f"Coverage: {covered}/{total} ({covered*100/total:.1f}%)")
    
    gaps = [(int(r['INSTRUCTION_MISSED']), f"{r['PACKAGE']}.{r['CLASS']}", int(r['LINE_MISSED']))
            for r in rows if int(r['INSTRUCTION_MISSED']) > 0]
    for m, cls, lines in sorted(gaps, reverse=True)[:5]:
        print(f"  {cls}: {lines} lines missing")
EOF
```

## Maven Integration

```bash
# Run tests
mvn test

# Run specific test
mvn test -Dtest=UserServiceTest

# With coverage
mvn test jacoco:report

# Skip tests (not recommended in TDD!)
mvn clean package -DskipTests
```

## References

See [Agent Skills Specification](https://agentskills.io/specification) for format details.
