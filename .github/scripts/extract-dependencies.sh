#!/bin/bash
# Extract Maven dependency updates and group them
# Usage: ./extract-dependencies.sh <outputfile>
# Example: ./extract-dependencies.sh dependency-review-2025-11-22T19-30-00.jsonl

set -euo pipefail

# Function: Extract dependencies from Maven
# Returns: Newline-separated JSON objects
extract_maven_dependencies() {
  # -DallowMajorUpdates=false: Only show minor and patch updates (no major version jumps)
  # -Dmaven.version.ignore: Filter out pre-release versions
  #   Covers: alpha, Alpha, ALPHA, beta, Beta, BETA, rc, RC, 
  #           milestone, Milestone, MILESTONE, snapshot, SNAPSHOT, M1-M9, CR1-CR9
  #   Supports both dash (-) and dot (.) separators: 1.0-Beta2 and 1.0.Beta2
  mvn versions:display-dependency-updates -B -Dversions.outputLineWidth=240 \
    -DallowMajorUpdates=false \
    -Dmaven.version.ignore=".*[-.]alpha.*,.*[-.]Alpha.*,.*[-.]ALPHA.*,.*[-.]beta.*,.*[-.]Beta.*,.*[-.]BETA.*,.*[-.]rc.*,.*[-.]RC.*,.*[-.]milestone.*,.*[-.]Milestone.*,.*[-.]MILESTONE.*,.*[-.]snapshot.*,.*[-.]SNAPSHOT.*,.*-M[0-9]+.*,.*\.CR[0-9]+.*" \
    2>/dev/null \
    | sort -u \
    | awk '/^\[INFO\]   [a-z].*:/ {
        gsub(/\.\.+/, " ");
        split($2, dep, ":");
        print "{\"groupId\":\"" dep[1] "\",\"artifactId\":\"" dep[2] "\",\"currentVersion\":\"" $3 "\",\"availableVersion\":\"" $5 "\"}"
      }' \
    | sort -u
}

# Function: Group dependencies by groupId and availableVersion
# Args: dependencies (newline-separated JSON objects)
# Returns: JSON arrays of dependency groups (one array per group)
group_dependencies() {
  local deps="$1"
  
  echo "$deps" | jq -s 'group_by(.groupId + "-" + .availableVersion) | .[]'
}

# Main execution
main() {
  # Check for output file argument
  if [ $# -ne 1 ]; then
    echo "Usage: $0 <outputfile>" >&2
    echo "Example: $0 dependency-review-2025-11-22T19-30-00.jsonl" >&2
    exit 1
  fi
  
  local groups_file="$1"
  
  echo "Extracting Maven dependency updates (minor/patch only, excluding pre-releases)..." >&2
  local dependencies
  dependencies=$(extract_maven_dependencies)
  
  echo "Grouping dependencies by groupId and availableVersion..." >&2
  local groups
  groups=$(group_dependencies "$dependencies")
  
  echo "$groups" | jq -c '.' > "$groups_file"
  
  echo "✓ Dependency groups file created: $groups_file" >&2
  echo "✓ Total groups: $(wc -l < "$groups_file")" >&2
  echo "" >&2
  echo "File location: $groups_file" >&2
}

main "$@"
