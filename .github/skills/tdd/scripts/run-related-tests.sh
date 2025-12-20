#!/usr/bin/env bash
set -euo pipefail

# run-related-tests.sh - Identify and run tests related to changed source files
# Usage: ./run-related-tests.sh <sourceFile>

if [ $# -ne 1 ]; then
    echo "Usage: $0 <sourceFile>" >&2
    echo "  sourceFile: Path to source file (e.g., src/main/java/com/example/UserService.java)" >&2
    echo "" >&2
    echo "Example: $0 src/main/java/com/example/service/UserService.java" >&2
    exit 1
fi

SOURCE_FILE="$1"

if [ ! -f "$SOURCE_FILE" ]; then
    echo "Error: Source file not found: $SOURCE_FILE" >&2
    exit 1
fi

PROJECT_ROOT="${PROJECT_ROOT:-$(git rev-parse --show-toplevel 2>/dev/null || pwd)}"
SRC_TEST="${PROJECT_ROOT}/src/test/java"

# Extract class name
CLASS_NAME=$(basename "$SOURCE_FILE" .java)

echo "=== Finding Related Tests ===" >&2
echo "Source file: $SOURCE_FILE" >&2
echo "Class name: $CLASS_NAME" >&2
echo "" >&2

# Find direct test file
DIRECT_TEST=$(find "$SRC_TEST" -name "${CLASS_NAME}Test.java" -o -name "${CLASS_NAME}*Test.java" 2>/dev/null | head -5)

# Find integration tests
INTEGRATION_TESTS=$(find "$SRC_TEST" -name "*IntegrationTest.java" -type f -exec grep -l "$CLASS_NAME" {} \; 2>/dev/null | head -5)

# Combine and deduplicate
ALL_TESTS=$(echo -e "$DIRECT_TEST\n$INTEGRATION_TESTS" | sort -u | grep -v "^$")

if [ -z "$ALL_TESTS" ]; then
    echo "No related tests found for $CLASS_NAME" >&2
    echo "Consider creating tests first!" >&2
    exit 1
fi

echo "Found related test files:" >&2
echo "$ALL_TESTS" | while read -r test_file; do
    TEST_CLASS=$(basename "$test_file" .java)
    echo "  - $TEST_CLASS" >&2
done
echo "" >&2

# Build test pattern for Maven
TEST_PATTERN=$(echo "$ALL_TESTS" | while read -r test_file; do
    basename "$test_file" .java
done | tr '\n' ',' | sed 's/,$//')

echo "Running tests: $TEST_PATTERN" >&2
echo "" >&2

cd "$PROJECT_ROOT"
mvn test -Dtest="$TEST_PATTERN" -DfailIfNoTests=false

exit $?
