#!/usr/bin/env bash
set -euo pipefail

# verify-url.sh - Verify URL returns 200 OK and contains relevant content
# Usage: ./verify-url.sh <url> <expectedContent>

if [ $# -ne 2 ]; then
    echo "Usage: $0 <url> <expectedContent>" >&2
    echo "Example: $0 'https://github.com/apache/commons-lang/releases/tag/rel/commons-lang-3.14.0' '3.14.0'" >&2
    exit 1
fi

URL="$1"
EXPECTED_CONTENT="$2"

# Create temporary file for response
TEMP_FILE=$(mktemp)
trap "rm -f $TEMP_FILE" EXIT

echo "Verifying URL: $URL" >&2

# Fetch URL and check HTTP status
HTTP_CODE=$(curl -sL --write-out "%{http_code}" --max-time 10 --retry 2 -o "$TEMP_FILE" "$URL" 2>/dev/null || echo "000")

if [ "$HTTP_CODE" != "200" ]; then
    echo "Error: URL returned HTTP $HTTP_CODE (expected 200)" >&2
    exit 1
fi

# Check if content contains expected text
if ! grep -qi "$EXPECTED_CONTENT" "$TEMP_FILE"; then
    echo "Warning: URL returned 200 OK but does not contain expected content: '$EXPECTED_CONTENT'" >&2
    echo "First 500 characters of response:" >&2
    head -c 500 "$TEMP_FILE" >&2
    exit 1
fi

echo "✓ URL verified: 200 OK with relevant content" >&2
exit 0
