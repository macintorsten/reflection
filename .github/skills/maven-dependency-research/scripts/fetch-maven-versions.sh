#!/usr/bin/env bash
set -euo pipefail

# fetch-maven-versions.sh - Query Maven Central for version history
# Usage: ./fetch-maven-versions.sh <groupId> <artifactId> <currentVersion> <availableVersion>

if [ $# -ne 4 ]; then
    echo "Usage: $0 <groupId> <artifactId> <currentVersion> <availableVersion>" >&2
    echo "Example: $0 org.apache.commons commons-lang3 3.12.0 3.14.0" >&2
    exit 1
fi

GROUP_ID="$1"
ARTIFACT_ID="$2"
CURRENT_VERSION="$3"
AVAILABLE_VERSION="$4"

# Convert groupId to path (e.g., org.apache.commons -> org/apache/commons)
GROUP_PATH="${GROUP_ID//./\/}"

echo "Fetching versions for $GROUP_ID:$ARTIFACT_ID from $CURRENT_VERSION to $AVAILABLE_VERSION" >&2

# Strategy 1: Try Maven Central metadata XML (fastest, most reliable)
METADATA_URL="https://repo1.maven.org/maven2/${GROUP_PATH}/${ARTIFACT_ID}/maven-metadata.xml"
echo "Attempting Strategy 1: Maven Central metadata XML..." >&2

VERSIONS=$(curl -sL --fail --retry 2 --max-time 10 "$METADATA_URL" 2>/dev/null | grep -oP '(?<=<version>)[^<]+' || echo "")

# Strategy 2: Try Maven Central Search API
if [ -z "$VERSIONS" ]; then
    echo "Strategy 1 failed. Attempting Strategy 2: Maven Central Search API..." >&2
    SEARCH_URL="https://search.maven.org/solrsearch/select?q=g:${GROUP_ID}+AND+a:${ARTIFACT_ID}&rows=200&wt=json"
    VERSIONS=$(curl -sL --fail --retry 2 --max-time 10 "$SEARCH_URL" 2>/dev/null | jq -r '.response.docs[].v' || echo "")
fi

if [ -z "$VERSIONS" ]; then
    echo "Error: Could not fetch versions from Maven Central" >&2
    echo "Please verify the dependency exists and try web search for version history" >&2
    exit 1
fi

# Filter versions to applicable range (greater than current, less than or equal to available)
# Exclude pre-release versions (alpha, beta, RC, M, SNAPSHOT)
echo "$VERSIONS" | sort -V | awk -v current="$CURRENT_VERSION" -v available="$AVAILABLE_VERSION" '
    function version_compare(v1, v2,    a1, a2, i) {
        split(v1, a1, /[.-]/)
        split(v2, a2, /[.-]/)
        for (i = 1; i <= length(a1) && i <= length(a2); i++) {
            if (a1[i] < a2[i]) return -1
            if (a1[i] > a2[i]) return 1
        }
        if (length(a1) < length(a2)) return -1
        if (length(a1) > length(a2)) return 1
        return 0
    }
    
    # Skip pre-release versions
    /alpha|beta|RC|M[0-9]|SNAPSHOT|rc|preview|milestone/ { next }
    
    # Keep versions: current < version <= available
    {
        if (version_compare($0, current) > 0 && version_compare($0, available) <= 0) {
            print $0
        }
    }
'

exit 0
