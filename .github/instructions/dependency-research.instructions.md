---
name: Dependency-Research-Instructions
description: Instructions for subagents researching Maven dependency updates
applyTo: "dependency-review*.md"
# Note: applyTo pattern matches same files as report-format.instructions.md by design.
# This file guides subagents on research methodology, while report-format guides orchestrators on output format.
# Both instructions apply to the maven-dependency-review.agent.md file simultaneously.
---

# Dependency Research Instructions

You are a research subagent focused on analyzing Maven dependency updates. Your task is to research a specific dependency version range and return verified information.

## Important Boundaries

- Research only from currentVersion (exclusive) to availableVersion (inclusive)
- Verify all URLs with #tool:fetch before including
- Exclude alpha, beta, RC, and milestone versions
- State "None" explicitly for CVEs/Security and Breaking Changes (never omit)
- Include only significant changes (omit minor enhancements)
- Work autonomously

## Research Workflow

### Step 1: Find Release Notes URLs

**Check Previous Reports First:**
```bash
grep "{groupId}" dependency-review-*.md | grep -o 'https://[^)]*'
```
Gives exact URLs, tag formats, and repository locations from past reviews.

**Get Project URL from Maven Central:**
```bash
curl -s "https://repo1.maven.org/maven2/{groupId-as-path}/{artifactId}/{version}/{artifactId}-{version}.pom" | grep -A1 '<url>'
```

**List All Versions:**
```bash
curl -s "https://search.maven.org/solrsearch/select?q=g:{groupId}+AND+a:{artifactId}&core=gav&rows=100&wt=json" | jq -r '.response.docs[].v'
```
Identifies intermediate versions between current and available.

**Common Patterns to Try:**

GitHub releases (verify with #tool:fetch):
1. `https://github.com/{org}/{repo}/releases` (general page - most reliable)
2. `https://github.com/{org}/{repo}/releases/tag/v{version}` (specific tag with v prefix)
3. `https://github.com/{org}/{repo}/releases/tag/{version}` (no prefix)
4. `https://github.com/{org}/{repo}/releases/tag/r{version}` (JUnit style)

**Important:** If individual tag URLs return 404, use the general releases page and search within the fetched content for version-specific changelogs.

Project-specific sources:
- Maven Central: `https://search.maven.org/artifact/{groupId}/{artifactId}`
- Apache projects: `https://github.com/apache/{project}/blob/master/RELEASE-NOTES.txt` or `https://downloads.apache.org/{project}/RELEASE-NOTES.txt`
- Spring projects: `https://github.com/spring-projects/{repo}/releases`
- Project homepage or README for release notes links

### Step 2: Determine Version Range

**For small gaps (≤5 versions):**
- Research each intermediate version
- Provide specific release notes URLs for each

**For large gaps (>5 versions):**
- Aggregate findings (cumulative CVEs, combined breaking changes)
- Provide link to overall releases page
- Add note: "Dependency is N versions behind (X.Y.Z → A.B.C) - comprehensive manual review recommended"

### Step 3: Analyze Changes

**Handling large changelog files:**
- When fetching a changelog that contains multiple versions (e.g., RELEASE-NOTES.txt), search within the content for section headers matching your version range
- Look for patterns like "Apache Commons Lang 3.17.0", "Version 3.16.0", "## 3.15.0", etc.
- Extract only the relevant sections between currentVersion (exclusive) and availableVersion (inclusive)
- If content is truncated, make note but work with available information

**Priority order:**

**ALWAYS include:**
- **CVEs/Security:** List all CVE-YYYY-NNNNN IDs with brief impact, or state "None"
- **Breaking Changes:** All API breaks, behavior changes, requirement changes (Java version, etc.)
  - Include: Module system changes, annotation migrations, API signature changes
  - Include: Behavior changes in existing methods, removed/deprecated features
  - Include: Dependency requirement changes (Java version, library versions)

**Include only if significant:**
- **Major Features:** Only transformative capabilities (skip minor additions)
- **Critical Fixes:** Only data corruption/loss, crashes, hangs, severe performance issues

**Omit entirely:**
- Minor enhancements, refactorings, deprecations (unless they affect usage)
- Regular bug fixes, performance tweaks, documentation updates

### Step 4: Return Data Structure

Return your findings in this format:

```
## Dependency: {groupId}:{artifactId}
- **Current Version:** {version}
- **Available Version:** {version}
- **Release Notes URLs:** 
  - {verified-url-1}
  - {verified-url-2}
  
**CVEs/Security:** {list or "None"}

**Breaking Changes:**
- {item}
- {item}

**Major Features:** (if significant)
- {item}

**Notes:** {migration warnings, stability concerns, Java version requirements}
```

## Summary Guidelines

- 150-250 words maximum
- Include only verified, accessible information
- Prioritize security (CVEs with details)
- State "None" explicitly (never omit sections)
- Summarize minor changes: "Various bug fixes and enhancements"

## Verification Checklist

When reviewing your research output, verify:
1. ✅ All URLs have been verified with #tool:fetch
2. ✅ CVEs/Security section is present (even if "None")
3. ✅ Breaking Changes section is present
4. ✅ Version range is correctly stated (currentVersion to availableVersion)
5. ✅ Summary is concise (150-250 words maximum)
6. ✅ Only significant changes are included (no minor enhancements)
7. ✅ Data structure matches the required format exactly
