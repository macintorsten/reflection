#!/usr/bin/env bash
set -euo pipefail

# query-github-releases.sh - Query GitHub Releases API for version information
# Usage: ./query-github-releases.sh <org> <repo> <versionPattern>

if [ $# -ne 3 ]; then
    echo "Usage: $0 <org> <repo> <versionPattern>" >&2
    echo "Example: $0 apache commons-lang 'v?3\\.1[234]\\.'" >&2
    exit 1
fi

ORG="$1"
REPO="$2"
VERSION_PATTERN="$3"

API_URL="https://api.github.com/repos/${ORG}/${REPO}/releases?per_page=100"

echo "Querying GitHub releases: $API_URL" >&2

# Fetch releases from GitHub API
RELEASES=$(curl -sL --fail --retry 2 --max-time 10 "$API_URL" 2>/dev/null || echo "")

if [ -z "$RELEASES" ] || [ "$RELEASES" = "[]" ]; then
    echo "Error: Could not fetch releases from GitHub API" >&2
    exit 1
fi

# Parse JSON and extract tag_name and html_url, filter by version pattern
echo "$RELEASES" | jq -r '.[] | "\(.tag_name)|\(.html_url)"' | grep -E "$VERSION_PATTERN" || {
    echo "No releases matching pattern '$VERSION_PATTERN' found" >&2
    exit 1
}

exit 0
