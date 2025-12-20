#!/usr/bin/env bash
set -euo pipefail

# analyze-coverage.sh - Generate and display test coverage for classes or packages
# Usage: ./analyze-coverage.sh [target]

TARGET="${1:-}"

PROJECT_ROOT="${PROJECT_ROOT:-$(git rev-parse --show-toplevel 2>/dev/null || pwd)}"
cd "$PROJECT_ROOT"

echo "=== Analyzing Test Coverage ===" >&2

# Check if jacoco plugin is configured
if ! grep -q "jacoco" pom.xml; then
    echo "Warning: JaCoCo plugin not configured in pom.xml" >&2
    echo "Add JaCoCo plugin to measure coverage" >&2
    echo "" >&2
fi

echo "Running tests with coverage..." >&2
mvn clean test jacoco:report -q

COVERAGE_EXIT_CODE=$?

if [ $COVERAGE_EXIT_CODE -ne 0 ]; then
    echo "Error: Tests failed or coverage report generation failed" >&2
    exit $COVERAGE_EXIT_CODE
fi

# Check if coverage report exists
COVERAGE_REPORT="${PROJECT_ROOT}/target/site/jacoco/index.html"
if [ ! -f "$COVERAGE_REPORT" ]; then
    echo "Warning: Coverage report not generated at $COVERAGE_REPORT" >&2
    echo "Ensure JaCoCo plugin is properly configured" >&2
    exit 1
fi

echo "" >&2
echo "✓ Coverage report generated" >&2
echo "Report location: $COVERAGE_REPORT" >&2
echo "" >&2

# Parse coverage from XML report if available
XML_REPORT="${PROJECT_ROOT}/target/site/jacoco/jacoco.xml"
if [ -f "$XML_REPORT" ]; then
    if command -v xmllint > /dev/null 2>&1; then
        # Extract overall coverage
        INSTRUCTION_MISSED=$(xmllint --xpath "sum(//counter[@type='INSTRUCTION']/@missed)" "$XML_REPORT" 2>/dev/null || echo "0")
        INSTRUCTION_COVERED=$(xmllint --xpath "sum(//counter[@type='INSTRUCTION']/@covered)" "$XML_REPORT" 2>/dev/null || echo "0")
        
        if [ "$INSTRUCTION_MISSED" != "0" ] || [ "$INSTRUCTION_COVERED" != "0" ]; then
            TOTAL=$((INSTRUCTION_MISSED + INSTRUCTION_COVERED))
            if [ "$TOTAL" -gt 0 ]; then
                COVERAGE=$((INSTRUCTION_COVERED * 100 / TOTAL))
                echo "Overall Coverage: ${COVERAGE}%" >&2
                echo "" >&2
            fi
        fi
    fi
    
    # If target specified, try to find specific class coverage
    if [ -n "$TARGET" ]; then
        echo "Coverage for: $TARGET" >&2
        if command -v grep > /dev/null 2>&1; then
            grep "$TARGET" "$XML_REPORT" | head -5 || echo "No specific data found"
        fi
        echo "" >&2
    fi
else
    echo "XML report not available for detailed analysis" >&2
fi

echo "Open the HTML report in your browser to see detailed coverage:" >&2
echo "  file://${COVERAGE_REPORT}" >&2

exit 0
