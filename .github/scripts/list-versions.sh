#!/bin/bash
# List all intermediate versions for a dependency from Maven Central
# Usage: ./list-versions.sh <groupId> <artifactId> <currentVersion> <availableVersion>
# Example: ./list-versions.sh com.jayway.jsonpath json-path 2.7.0 2.9.0

set -euo pipefail

# Embedded test data for unit tests
read -r -d '' TEST_DATA <<'EOF' || true
com.jayway.jsonpath json-path 2.7.0 2.9.0
org.apache.commons commons-lang3 3.12.0 3.14.0
EOF

# Function: Fetch all versions from Maven Central for a given dependency
# Args: groupId, artifactId
# Returns: JSON response from Maven Central
fetch_maven_versions() {
  local group_id="$1"
  local artifact_id="$2"
  
  curl -s "https://search.maven.org/solrsearch/select?q=g:${group_id}+AND+a:${artifact_id}&core=gav&rows=300&wt=json" 2>/dev/null
}

# Function: Extract and filter versions from Maven response
# Args: maven_response
# Returns: Newline-separated list of stable versions, sorted
extract_stable_versions() {
  local maven_response="$1"
  
  echo "$maven_response" \
    | jq -r '.response.docs[].v' \
    | grep -v -E '(alpha|beta|rc|RC|M[0-9]|CR[0-9])' \
    | sort -V
}

# Function: Filter versions by range (current < version <= available)
# Args: all_versions (newline-separated), current_version, available_version
# Returns: Newline-separated list of versions in range (including availableVersion)
filter_versions_by_range() {
  local all_versions="$1"
  local current_version="$2"
  local available_version="$3"
  
  echo "$all_versions" | awk -v cur="$current_version" -v avail="$available_version" '
    {
      # Compare version with current (version > current)
      cmd1 = "printf \"%s\\n%s\" \"" cur "\" \"" $0 "\" | sort -V | tail -1"
      cmd1 | getline max1
      close(cmd1)
      
      # Compare version with available (version <= available)
      cmd2 = "printf \"%s\\n%s\" \"" avail "\" \"" $0 "\" | sort -V | head -1"
      cmd2 | getline min2
      close(cmd2)
      
      # Print if version is greater than current and less than or equal to available
      if (max1 == $0 && $0 != cur && min2 == $0) {
        print $0
      }
    }'
}

# Function: List versions for a dependency
# Args: groupId, artifactId, currentVersion, availableVersion
# Returns: Newline-separated list of intermediate versions
list_versions() {
  local group_id="$1"
  local artifact_id="$2"
  local current_version="$3"
  local available_version="$4"
  
  # Fetch all versions from Maven Central
  local maven_response
  maven_response=$(fetch_maven_versions "$group_id" "$artifact_id")
  
  # Extract stable versions
  local all_versions
  all_versions=$(extract_stable_versions "$maven_response")
  
  # Filter by version range (including availableVersion)
  filter_versions_by_range "$all_versions" "$current_version" "$available_version"
}

# Function: Run unit tests
run_unit_tests() {
  echo "=== Running Unit Tests ===" >&2
  echo "" >&2
  
  while IFS= read -r line; do
    [ -z "$line" ] && continue
    
    read -r group_id artifact_id current_version available_version <<< "$line"
    
    echo "Test: ${group_id}:${artifact_id} (${current_version} -> ${available_version})" >&2
    local versions
    versions=$(list_versions "$group_id" "$artifact_id" "$current_version" "$available_version")
    
    if [ -n "$versions" ]; then
      echo "  Intermediate versions:" >&2
      echo "$versions" | while read -r v; do
        echo "    - $v" >&2
      done
    else
      echo "  No intermediate versions found" >&2
    fi
    echo "" >&2
  done <<< "$TEST_DATA"
  
  echo "✓ Unit tests completed" >&2
}

# Main execution
main() {
  # Check if running in test mode
  if [ "${1:-}" = "--test" ]; then
    run_unit_tests
    exit 0
  fi
  
  # Check for required arguments
  if [ $# -ne 4 ]; then
    echo "Usage: $0 <groupId> <artifactId> <currentVersion> <availableVersion>" >&2
    echo "       $0 --test (run unit tests)" >&2
    echo "Example: $0 com.jayway.jsonpath json-path 2.7.0 2.9.0" >&2
    exit 1
  fi
  
  local group_id="$1"
  local artifact_id="$2"
  local current_version="$3"
  local available_version="$4"
  
  list_versions "$group_id" "$artifact_id" "$current_version" "$available_version"
}

main "$@"
