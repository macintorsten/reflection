#!/usr/bin/env bash
set -euo pipefail

# run-targeted-tests.sh - Run specific test class or method for rapid feedback
# Usage: ./run-targeted-tests.sh <testPattern>

if [ $# -ne 1 ]; then
    echo "Usage: $0 <testPattern>" >&2
    echo "  testPattern: Test class name or pattern (e.g., UserServiceTest, *ServiceTest)" >&2
    echo "" >&2
    echo "Examples:" >&2
    echo "  $0 UserServiceTest" >&2
    echo "  $0 'com.example.service.*Test'" >&2
    exit 1
fi

TEST_PATTERN="$1"

PROJECT_ROOT="${PROJECT_ROOT:-$(git rev-parse --show-toplevel 2>/dev/null || pwd)}"
cd "$PROJECT_ROOT"

echo "=== Running Targeted Tests ===" >&2
echo "Pattern: $TEST_PATTERN" >&2
echo "" >&2

# Run Maven test with specific pattern
mvn test -Dtest="$TEST_PATTERN" -DfailIfNoTests=false

TEST_EXIT_CODE=$?

echo "" >&2
if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo "✓ Tests passed" >&2
else
    echo "✗ Tests failed" >&2
fi

exit $TEST_EXIT_CODE
