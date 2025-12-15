#!/bin/bash
# Script to test the documentation locally

set -e

DOCS_DIR="docs"

echo "=== Testing Documentation Setup ==="
echo ""

# 1. Check if required files exist
echo "Step 1: Checking required files..."
if [ ! -f "${DOCS_DIR}/index.html" ]; then
    echo "✗ Error: ${DOCS_DIR}/index.html not found"
    exit 1
fi
echo "✓ index.html exists"

if [ ! -f "${DOCS_DIR}/openapi.json" ]; then
    echo "✗ Error: ${DOCS_DIR}/openapi.json not found"
    echo "  Run .github/scripts/generate-openapi-spec.sh first to generate it"
    exit 1
fi
echo "✓ openapi.json exists"
echo ""

# 2. Validate OpenAPI spec
echo "Step 2: Validating OpenAPI specification..."
if ! python3 -m json.tool "${DOCS_DIR}/openapi.json" > /dev/null 2>&1; then
    echo "✗ Error: openapi.json is not valid JSON"
    exit 1
fi
echo "✓ OpenAPI spec is valid JSON"

# Extract and display API info
API_INFO=$(python3 -c "
import json
with open('${DOCS_DIR}/openapi.json') as f:
    spec = json.load(f)
    print(f\"Title: {spec['info']['title']}\")
    print(f\"Version: {spec['info']['version']}\")
    print(f\"Endpoints: {len(spec['paths'])}\")
    for path in spec['paths']:
        methods = ', '.join(spec['paths'][path].keys())
        print(f\"  - {path} ({methods})\")
")
echo "$API_INFO"
echo ""

# 3. Check HTML references OpenAPI spec correctly
echo "Step 3: Checking HTML configuration..."
if grep -q 'url: "openapi.json"' "${DOCS_DIR}/index.html"; then
    echo "✓ HTML correctly references openapi.json"
else
    echo "✗ Error: HTML does not reference openapi.json correctly"
    exit 1
fi

if grep -q 'supportedSubmitMethods: \[\]' "${DOCS_DIR}/index.html"; then
    echo "✓ Interactive features are disabled (supportedSubmitMethods: [])"
else
    echo "⚠ Warning: Interactive features may be enabled"
fi
echo ""

# 4. Start local server
echo "Step 4: Starting local HTTP server..."
echo "To view the documentation:"
echo "  1. Run: python3 -m http.server 8000 --directory ${DOCS_DIR}"
echo "  2. Open: http://localhost:8000"
echo ""
echo "Or use npx:"
echo "  npx serve ${DOCS_DIR}"
echo ""

echo "=== All checks passed! ==="
