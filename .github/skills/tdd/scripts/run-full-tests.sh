#!/usr/bin/env bash
set -euo pipefail

# run-full-tests.sh - Run complete test suite with formatted output
# Usage: ./run-full-tests.sh

PROJECT_ROOT="${PROJECT_ROOT:-$(git rev-parse --show-toplevel 2>/dev/null || pwd)}"
cd "$PROJECT_ROOT"

echo "=== Running Full Test Suite ===" >&2
echo "" >&2

# Run all tests
mvn test

TEST_EXIT_CODE=$?

echo "" >&2
echo "=== Test Summary ===" >&2

# Parse test results if available
SUREFIRE_REPORTS="${PROJECT_ROOT}/target/surefire-reports"
if [ -d "$SUREFIRE_REPORTS" ]; then
    # Count test results
    TOTAL_TESTS=$(find "$SUREFIRE_REPORTS" -name "TEST-*.xml" -exec grep -h "tests=" {} \; 2>/dev/null | \
        sed 's/.*tests="\([0-9]*\)".*/\1/' | awk '{s+=$1} END {print s}')
    FAILURES=$(find "$SUREFIRE_REPORTS" -name "TEST-*.xml" -exec grep -h "failures=" {} \; 2>/dev/null | \
        sed 's/.*failures="\([0-9]*\)".*/\1/' | awk '{s+=$1} END {print s}')
    ERRORS=$(find "$SUREFIRE_REPORTS" -name "TEST-*.xml" -exec grep -h "errors=" {} \; 2>/dev/null | \
        sed 's/.*errors="\([0-9]*\)".*/\1/' | awk '{s+=$1} END {print s}')
    SKIPPED=$(find "$SUREFIRE_REPORTS" -name "TEST-*.xml" -exec grep -h "skipped=" {} \; 2>/dev/null | \
        sed 's/.*skipped="\([0-9]*\)".*/\1/' | awk '{s+=$1} END {print s}')
    
    echo "Total Tests: ${TOTAL_TESTS:-0}" >&2
    echo "Passed: $((${TOTAL_TESTS:-0} - ${FAILURES:-0} - ${ERRORS:-0} - ${SKIPPED:-0}))" >&2
    echo "Failed: ${FAILURES:-0}" >&2
    echo "Errors: ${ERRORS:-0}" >&2
    echo "Skipped: ${SKIPPED:-0}" >&2
fi

echo "" >&2
if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo "✓ All tests passed" >&2
else
    echo "✗ Some tests failed" >&2
fi

exit $TEST_EXIT_CODE
