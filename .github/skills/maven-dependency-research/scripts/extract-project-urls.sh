#!/usr/bin/env bash
set -euo pipefail

# extract-project-urls.sh - Extract project homepage and SCM URLs from Maven POM
# Usage: ./extract-project-urls.sh <groupId> <artifactId> <version>

if [ $# -ne 3 ]; then
    echo "Usage: $0 <groupId> <artifactId> <version>" >&2
    echo "Example: $0 org.apache.commons commons-lang3 3.14.0" >&2
    exit 1
fi

GROUP_ID="$1"
ARTIFACT_ID="$2"
VERSION="$3"

# Convert groupId to path (e.g., org.apache.commons -> org/apache/commons)
GROUP_PATH="${GROUP_ID//./\/}"

POM_URL="https://repo1.maven.org/maven2/${GROUP_PATH}/${ARTIFACT_ID}/${VERSION}/${ARTIFACT_ID}-${VERSION}.pom"

echo "Fetching POM from: $POM_URL" >&2

# Download POM and extract URLs
POM_CONTENT=$(curl -sL --fail --retry 2 --max-time 10 "$POM_URL" 2>/dev/null || echo "")

if [ -z "$POM_CONTENT" ]; then
    echo "Error: Could not fetch POM from Maven Central" >&2
    exit 1
fi

# Extract <url> tags
echo "$POM_CONTENT" | grep -oP '(?<=<url>)[^<]+' | grep -E '^https?://' || true

# Extract <connection> and <developerConnection> tags from <scm>
echo "$POM_CONTENT" | grep -oP '(?<=<connection>)[^<]+' | sed 's/scm:git://g; s/scm:svn://g; s/.git$//g' | grep -E '^https?://' || true
echo "$POM_CONTENT" | grep -oP '(?<=<developerConnection>)[^<]+' | sed 's/scm:git://g; s/scm:svn://g; s/.git$//g' | grep -E '^https?://' || true

# Sort and deduplicate
exit 0
