#!/usr/bin/env bash
set -euo pipefail

# discover-test-patterns.sh - Analyze existing tests to discover project conventions
# Usage: ./discover-test-patterns.sh [sourceFile]

PROJECT_ROOT="${PROJECT_ROOT:-$(git rev-parse --show-toplevel 2>/dev/null || pwd)}"
TEST_DIR="${PROJECT_ROOT}/src/test/java"

echo "=== Test Pattern Discovery ===" >&2
echo "" >&2

if [ ! -d "$TEST_DIR" ]; then
    echo "Error: Test directory not found at $TEST_DIR" >&2
    exit 1
fi

# Discover test framework and annotations
echo "## Test Framework" >&2
if grep -r "@Test" "$TEST_DIR" --include="*.java" -l | head -1 | xargs grep -l "org.junit.jupiter" > /dev/null 2>&1; then
    echo "Framework: JUnit 5 (Jupiter)" >&2
    TEST_FRAMEWORK="junit5"
elif grep -r "@Test" "$TEST_DIR" --include="*.java" -l | head -1 | xargs grep -l "org.junit" > /dev/null 2>&1; then
    echo "Framework: JUnit 4" >&2
    TEST_FRAMEWORK="junit4"
else
    echo "Framework: Unknown/Not detected" >&2
    TEST_FRAMEWORK="unknown"
fi
echo "" >&2

# Discover assertion library
echo "## Assertion Library" >&2
if grep -r "assertThat" "$TEST_DIR" --include="*.java" | grep -i "assertj" -q 2>/dev/null; then
    echo "Assertions: AssertJ (assertThat)" >&2
    ASSERTION_STYLE="assertj"
elif grep -r "assertThat" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
    echo "Assertions: Hamcrest (assertThat)" >&2
    ASSERTION_STYLE="hamcrest"
elif grep -r "assertEquals\|assertTrue\|assertNotNull" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
    echo "Assertions: JUnit assertions" >&2
    ASSERTION_STYLE="junit"
else
    echo "Assertions: Not detected" >&2
    ASSERTION_STYLE="unknown"
fi
echo "" >&2

# Discover mocking framework
echo "## Mocking Framework" >&2
if grep -r "@Mock\|@InjectMocks" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
    echo "Mocking: Mockito (annotations)" >&2
    MOCKING_STYLE="mockito-annotations"
elif grep -r "Mockito\.mock\|Mockito\.when" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
    echo "Mocking: Mockito (programmatic)" >&2
    MOCKING_STYLE="mockito-programmatic"
else
    echo "Mocking: None or not detected" >&2
    MOCKING_STYLE="none"
fi
echo "" >&2

# Discover Spring Boot testing patterns
echo "## Spring Boot Testing" >&2
if grep -r "@SpringBootTest" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
    echo "Integration Tests: @SpringBootTest" >&2
    HAS_SPRING_TESTS="true"
else
    echo "Integration Tests: None detected" >&2
    HAS_SPRING_TESTS="false"
fi

if grep -r "@WebMvcTest" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
    echo "Controller Tests: @WebMvcTest" >&2
fi

if grep -r "@DataJpaTest" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
    echo "Repository Tests: @DataJpaTest" >&2
fi

if grep -r "Testcontainers\|@Container" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
    echo "Database Tests: Testcontainers" >&2
    HAS_TESTCONTAINERS="true"
else
    HAS_TESTCONTAINERS="false"
fi
echo "" >&2

# Discover test naming conventions
echo "## Test Naming Conventions" >&2
SAMPLE_TESTS=$(find "$TEST_DIR" -name "*Test.java" -type f | head -5)
if [ -n "$SAMPLE_TESTS" ]; then
    echo "Sample test methods:" >&2
    echo "$SAMPLE_TESTS" | while read -r test_file; do
        grep -E "^\s*void [a-zA-Z]" "$test_file" 2>/dev/null | head -3 | sed 's/.*void /  - /' | sed 's/(.*/()/' || true
    done
fi
echo "" >&2

# Analyze test structure
echo "## Test Structure Patterns" >&2
if grep -r "// Given\|// When\|// Then" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
    echo "Structure: Given-When-Then (with comments)" >&2
    TEST_STRUCTURE="given-when-then-comments"
elif grep -r "// Arrange\|// Act\|// Assert" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
    echo "Structure: Arrange-Act-Assert (with comments)" >&2
    TEST_STRUCTURE="arrange-act-assert-comments"
else
    echo "Structure: Implicit (no section comments)" >&2
    TEST_STRUCTURE="implicit"
fi
echo "" >&2

# Discover test lifecycle methods
echo "## Test Lifecycle" >&2
if [ "$TEST_FRAMEWORK" = "junit5" ]; then
    if grep -r "@BeforeEach" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
        echo "Setup: @BeforeEach" >&2
    fi
    if grep -r "@BeforeAll" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
        echo "Class Setup: @BeforeAll" >&2
    fi
    if grep -r "@AfterEach" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
        echo "Teardown: @AfterEach" >&2
    fi
elif [ "$TEST_FRAMEWORK" = "junit4" ]; then
    if grep -r "@Before" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
        echo "Setup: @Before" >&2
    fi
    if grep -r "@After" "$TEST_DIR" --include="*.java" | head -1 > /dev/null 2>&1; then
        echo "Teardown: @After" >&2
    fi
fi
echo "" >&2

# Find base test classes
echo "## Base Test Classes" >&2
BASE_CLASSES=$(find "$TEST_DIR" -name "Abstract*.java" -o -name "Base*.java" 2>/dev/null | head -5)
if [ -n "$BASE_CLASSES" ]; then
    echo "$BASE_CLASSES" | while read -r base_class; do
        basename "$base_class" | sed 's/^/  - /'
    done
else
    echo "  - None found" >&2
fi
echo "" >&2

# If specific source file provided, find corresponding test
if [ $# -eq 1 ]; then
    SOURCE_FILE="$1"
    echo "## Analysis for: $SOURCE_FILE" >&2
    
    # Extract class name from source file
    CLASS_NAME=$(basename "$SOURCE_FILE" .java)
    
    # Find potential test files
    echo "Searching for tests related to: $CLASS_NAME" >&2
    RELATED_TESTS=$(find "$TEST_DIR" -name "${CLASS_NAME}Test.java" -o -name "${CLASS_NAME}*Test.java" 2>/dev/null)
    
    if [ -n "$RELATED_TESTS" ]; then
        echo "Found test files:" >&2
        echo "$RELATED_TESTS" | while read -r test_file; do
            echo "  - $test_file" >&2
            # Count test methods
            TEST_COUNT=$(grep -c "@Test" "$test_file" 2>/dev/null || echo "0")
            echo "    Test methods: $TEST_COUNT" >&2
        done
    else
        echo "No existing test files found for $CLASS_NAME" >&2
        echo "Suggested test file: ${TEST_DIR}/$(echo "$SOURCE_FILE" | sed "s|${PROJECT_ROOT}/src/main/java/||" | sed "s/.java$/Test.java/")" >&2
    fi
    echo "" >&2
fi

# Output summary in machine-readable format
echo "## Summary (JSON)" >&2
cat << EOF
{
  "testFramework": "$TEST_FRAMEWORK",
  "assertionStyle": "$ASSERTION_STYLE",
  "mockingStyle": "$MOCKING_STYLE",
  "hasSpringTests": $HAS_SPRING_TESTS,
  "hasTestcontainers": $HAS_TESTCONTAINERS,
  "testStructure": "$TEST_STRUCTURE"
}
EOF

exit 0
