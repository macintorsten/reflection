#!/bin/bash
# Research a single dependency group

GROUP_JSON="$1"
GROUP_ID=$(echo "$GROUP_JSON" | jq -r '.[0].groupId')
ARTIFACT_ID=$(echo "$GROUP_JSON" | jq -r '.[0].artifactId')
CURRENT_VER=$(echo "$GROUP_JSON" | jq -r '.[0].currentVersion')
AVAILABLE_VER=$(echo "$GROUP_JSON" | jq -r '.[0].availableVersion')

echo "## Dependency: ${GROUP_ID}:${ARTIFACT_ID}"
echo "**Current Version:** ${CURRENT_VER}"
echo "**Available Version:** ${AVAILABLE_VER}"

# Phase 2: Get versions from Maven metadata
GROUP_PATH=$(echo "$GROUP_ID" | tr '.' '/')
META_URL="https://repo1.maven.org/maven2/${GROUP_PATH}/${ARTIFACT_ID}/maven-metadata.xml"

echo "Fetching versions..."
VERSIONS=$(curl -sL --fail --retry 2 --max-time 10 "$META_URL" 2>/dev/null | grep '<version>' | sed 's/.*<version>\(.*\)<\/version>/\1/' | sort -V)

if [ -z "$VERSIONS" ]; then
  echo "**Release Notes:** Unable to fetch versions"
  echo "**CVEs/Security:** Research incomplete"
  echo "**Breaking Changes:** Research incomplete"
  echo ""
  exit 0
fi

# Filter for applicable versions (between current and available, exclude prereleases)
APPLICABLE=$(echo "$VERSIONS" | awk -v curr="$CURRENT_VER" -v avail="$AVAILABLE_VER" '
  $0 > curr && $0 <= avail && 
  $0 !~ /[Aa]lpha/ && $0 !~ /[Bb]eta/ && $0 !~ /[Rr][Cc]/ && 
  $0 !~ /[Mm]ilestone/ && $0 !~ /SNAPSHOT/ && $0 !~ /-M[0-9]/ && $0 !~ /\.CR[0-9]/
')

VERSION_COUNT=$(echo "$APPLICABLE" | grep -c .)
echo "Applicable versions: $VERSION_COUNT"
echo "$APPLICABLE" | head -10

# Phase 3: Find release notes
POM_URL="https://repo1.maven.org/maven2/${GROUP_PATH}/${ARTIFACT_ID}/${AVAILABLE_VER}/${ARTIFACT_ID}-${AVAILABLE_VER}.pom"
echo ""
echo "Checking POM for project URL..."
PROJECT_URL=$(curl -sL --fail --retry 2 --max-time 10 "$POM_URL" 2>/dev/null | grep -oP '(?<=<url>)[^<]+' | head -1)
SCM_URL=$(curl -sL --fail --retry 2 --max-time 10 "$POM_URL" 2>/dev/null | grep -oP '(?<=<connection>)[^<]+' | head -1 | sed 's|scm:git:||' | sed 's|\.git||')

echo "Project URL: $PROJECT_URL"
echo "SCM URL: $SCM_URL"
echo ""
