# Test Report Template

**Generated:** {{TIMESTAMP}}  
**Test Type:** {{TEST_TYPE}} (Unit/Integration/E2E)  
**Component:** {{COMPONENT_NAME}}

---

## Test Summary

**Total Tests:** {{TOTAL_TESTS}}  
**Passed:** {{PASSED_TESTS}}  
**Failed:** {{FAILED_TESTS}}  
**Skipped:** {{SKIPPED_TESTS}}  
**Success Rate:** {{SUCCESS_RATE}}%

**Execution Time:** {{EXECUTION_TIME}} seconds

---

## Test Coverage

**Line Coverage:** {{LINE_COVERAGE}}%  
**Branch Coverage:** {{BRANCH_COVERAGE}}%  
**Method Coverage:** {{METHOD_COVERAGE}}%

**Coverage Goal:** {{COVERAGE_GOAL}}% (Target for this component)

### Coverage Status
- ✅ Met minimum coverage threshold
- ⚠️ Below recommended coverage
- ❌ Critical paths not covered

---

## Test Results by Category

### Unit Tests
- **Total:** {{UNIT_TOTAL}}
- **Passed:** {{UNIT_PASSED}}
- **Failed:** {{UNIT_FAILED}}

### Integration Tests
- **Total:** {{INTEGRATION_TOTAL}}
- **Passed:** {{INTEGRATION_PASSED}}
- **Failed:** {{INTEGRATION_FAILED}}

---

## Failed Tests

{{#if FAILED_TESTS}}
### {{TEST_CLASS}}::{{TEST_METHOD}}

**Status:** ❌ Failed  
**Error:** {{ERROR_MESSAGE}}

**Stack Trace:**
```
{{STACK_TRACE}}
```

**Failure Reason:**
{{FAILURE_ANALYSIS}}

**Suggested Fix:**
{{SUGGESTED_FIX}}

---
{{else}}
✅ All tests passed!
{{/if}}

---

## Test Quality Metrics

### Test Independence
- ✅ All tests can run in isolation
- ✅ No shared mutable state
- ✅ Proper setup/teardown

### Test Clarity
- ✅ Descriptive test names (should...When...)
- ✅ Clear Given-When-Then structure
- ✅ Meaningful assertions

### Test Performance
- **Fastest Test:** {{FASTEST_TEST}} ({{FASTEST_TIME}}ms)
- **Slowest Test:** {{SLOWEST_TEST}} ({{SLOWEST_TIME}}ms)
- **Average Test Time:** {{AVERAGE_TIME}}ms

⚠️ **Slow Tests** (>1s):
{{#each SLOW_TESTS}}
- {{TEST_NAME}} ({{DURATION}}ms)
{{/each}}

---

## Coverage Gaps

### Uncovered Code

**Lines Not Covered:** {{UNCOVERED_LINES_COUNT}}

{{#each UNCOVERED_AREAS}}
**File:** {{FILE_PATH}}  
**Lines:** {{LINE_RANGE}}

**Code:**
```java
{{CODE_SNIPPET}}
```

**Recommendation:** {{RECOMMENDATION}}

---
{{/each}}

### Missing Test Scenarios

Identified scenarios that should be tested:

1. **Edge Case:** {{EDGE_CASE_DESCRIPTION}}
   - Current Coverage: ❌ Not tested
   - Priority: High
   - Suggested Test: `shouldHandle{{Scenario}}When{{Condition}}`

2. **Error Path:** {{ERROR_PATH_DESCRIPTION}}
   - Current Coverage: ❌ Not tested
   - Priority: High
   - Suggested Test: `shouldThrowExceptionWhen{{Condition}}`

3. **Integration Path:** {{INTEGRATION_DESCRIPTION}}
   - Current Coverage: ⚠️ Partially tested
   - Priority: Medium
   - Suggested Test: `should{{Behavior}}WithReal{{Dependency}}`

---

## TDD Cycle Compliance

### Red Phase ✅
- Test was written first
- Test failed for the right reason
- Failure message was clear

### Green Phase ✅
- Minimal implementation made test pass
- No over-engineering
- Test now passes

### Refactor Phase ✅
- Code was refactored
- Tests still pass
- Code quality improved

---

## Test Smells Detected

{{#if TEST_SMELLS}}
### ⚠️ Issues Found

1. **{{SMELL_TYPE}}** in `{{TEST_CLASS}}`
   - **Description:** {{SMELL_DESCRIPTION}}
   - **Location:** Line {{LINE_NUMBER}}
   - **Impact:** {{IMPACT}}
   - **Recommendation:** {{FIX_RECOMMENDATION}}

{{else}}
✅ No test smells detected
{{/if}}

---

## Dependencies Used in Tests

### Mocking Frameworks
- Mockito {{VERSION}}

### Assertion Libraries
- AssertJ {{VERSION}}
- JUnit Jupiter Assertions

### Test Containers
- Testcontainers {{VERSION}}
- PostgreSQL Container

### Other Test Dependencies
- Spring Boot Test
- MockMvc
- JsonPath

---

## Recommendations

### Immediate Actions
1. {{ACTION_1}}
2. {{ACTION_2}}
3. {{ACTION_3}}

### Code Quality Improvements
- {{IMPROVEMENT_1}}
- {{IMPROVEMENT_2}}

### Coverage Improvements
- Add tests for {{MISSING_COVERAGE_AREA}}
- Cover edge cases in {{COMPONENT}}

### Performance Optimizations
- Optimize {{SLOW_TEST}} (currently {{DURATION}}ms)
- Consider using @MockBean instead of full context

---

## Next Steps

**For Red Phase:**
- [ ] Write next failing test
- [ ] Verify test fails for correct reason

**For Green Phase:**
- [ ] Implement minimal code to pass test
- [ ] Run test to verify it passes
- [ ] Run all related tests

**For Refactor Phase:**
- [ ] Identify code smells
- [ ] Refactor implementation
- [ ] Refactor tests if needed
- [ ] Verify all tests still pass
- [ ] Check coverage hasn't decreased

---

## Test Execution Environment

**Java Version:** {{JAVA_VERSION}}  
**Maven Version:** {{MAVEN_VERSION}}  
**Spring Boot Version:** {{SPRING_BOOT_VERSION}}  
**JUnit Version:** {{JUNIT_VERSION}}

**Build Tool:** Maven  
**CI/CD:** {{CI_SYSTEM}} (if applicable)

---

## Appendix

### Test File Locations

**Unit Tests:**
- {{UNIT_TEST_PATH_1}}
- {{UNIT_TEST_PATH_2}}

**Integration Tests:**
- {{INTEGRATION_TEST_PATH_1}}
- {{INTEGRATION_TEST_PATH_2}}

### Coverage Report Location

HTML Report: `target/site/jacoco/index.html`  
XML Report: `target/site/jacoco/jacoco.xml`

### Surefire Reports

Location: `target/surefire-reports/`

---

**Report Generated By:** TDD Skill Agent  
**Report Version:** 1.0.0  
**Generation Time:** {{GENERATION_TIMESTAMP}}
