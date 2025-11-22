#!/bin/bash
# Extract Maven dependency updates and fetch all intermediate versions from Maven Central
# Usage: ./extract-dependencies.sh <outputfile>
# Example: ./extract-dependencies.sh dependency-review-2025-11-22T19-30-00.jsonl

set -euo pipefail

# Check for output file argument
if [ $# -ne 1 ]; then
  echo "Usage: $0 <outputfile>" >&2
  echo "Example: $0 dependency-review-2025-11-22T19-30-00.jsonl" >&2
  exit 1
fi

GROUPS_FILE="/workspace/$1"
TEMP_FILE="/tmp/deps-$$.json"

echo "Extracting Maven dependency updates..."

# Run Maven to get available updates (exclude major upgrades and unstable versions)
mvn versions:display-dependency-updates -B -Dversions.outputLineWidth=240 \
  -DallowMajorUpdates=false \
  -Dmaven.version.ignore=".*-alpha.*,.*-beta.*,.*-rc.*,.*-RC.*,.*-M[0-9]+.*,.*\.CR[0-9]+.*" \
  2>/dev/null \
  | sort -u \
  | awk '/^\[INFO\]   [a-z].*:/ {
      gsub(/\.\.+/, " ");
      split($2, dep, ":");
      print "{\"groupId\":\"" dep[1] "\",\"artifactId\":\"" dep[2] "\",\"currentVersion\":\"" $3 "\",\"availableVersion\":\"" $5 "\"}"
    }' \
  | sort -u \
  > "$TEMP_FILE"

echo "Fetching all intermediate versions from Maven Central..."

# Process each dependency and add versions list
jq -c '.' "$TEMP_FILE" | while IFS= read -r dep; do
  GROUP_ID=$(echo "$dep" | jq -r '.groupId')
  ARTIFACT_ID=$(echo "$dep" | jq -r '.artifactId')
  CURRENT_VERSION=$(echo "$dep" | jq -r '.currentVersion')
  AVAILABLE_VERSION=$(echo "$dep" | jq -r '.availableVersion')
  
  # Convert groupId to path format (e.g., org.springframework -> org/springframework)
  GROUP_PATH="${GROUP_ID//.//}"
  
  # Fetch all versions from Maven Central
  ALL_VERSIONS=$(curl -s "https://search.maven.org/solrsearch/select?q=g:${GROUP_ID}+AND+a:${ARTIFACT_ID}&core=gav&rows=1000&wt=json" \
    | jq -r '.response.docs[].v' \
    | grep -v -E '(alpha|beta|rc|RC|M[0-9]|CR[0-9])' \
    | sort -V)
  
  # Filter to version range: > currentVersion and <= availableVersion
  FILTERED_VERSIONS=$(echo "$ALL_VERSIONS" | awk -v cur="$CURRENT_VERSION" -v avail="$AVAILABLE_VERSION" '
    {
      if ($0 > cur && $0 <= avail) {
        print $0
      }
    }' | jq -R . | jq -s .)
  
  # Add versions array to dependency object
  echo "$dep" | jq --argjson versions "$FILTERED_VERSIONS" '. + {versions: $versions}'
done | jq -c -s 'group_by(.groupId + "-" + .availableVersion)[]' > "$GROUPS_FILE"

rm -f "$TEMP_FILE"

echo "✓ Dependency groups file created: $GROUPS_FILE"
echo "✓ Total groups: $(wc -l < "$GROUPS_FILE")"
echo ""
echo "File location: $GROUPS_FILE"
