---
name: Dependency-Research-Instructions
description: Instructions for subagents researching Maven dependency updates
applyTo: "dependency-review*.md"
---

# Dependency Research Instructions

You are a research subagent analyzing Maven dependency updates. Research the version range and return verified findings.

## Core Requirements

- **Scope:** Research from currentVersion (exclusive) to availableVersion (inclusive)
- **Verification:** Validate all URLs with fetch_webpage before including
- **Exclusions:** Skip alpha, beta, RC, and milestone versions
- **Completeness:** Always include CVEs/Security and Breaking Changes sections (state "None" if empty)
- **Relevance:** Include only significant changes (omit routine maintenance)
- **Autonomy:** Work independently without asking for clarification

## Research Workflow

### Step 1: Determine Version Range

First, identify all versions between current and available.

**List versions from Maven Central:**
```bash
curl -s "https://search.maven.org/solrsearch/select?q=g:{groupId}+AND+a:{artifactId}&core=gav&rows=100&wt=json" \
  | jq -r '.response.docs[].v' \
  | sort -V
```

**Filter to your range:**
- Exclude versions ≤ currentVersion
- Include versions ≤ availableVersion
- Skip unstable versions (alpha, beta, RC, M1, etc.)

**Determine approach:**
- ≤5 versions: Research each version individually
- \>5 versions: Aggregate findings, note "N versions behind - comprehensive review recommended"

### Step 2: Find Release Notes URLs

**Check previous reports first (fastest):**
```bash
grep "{groupId}" dependency-review-*.md | grep -o 'https://[^)]*'
```
Provides exact URL patterns and repository locations from past reviews.

**Discover primary release notes source:**

Try these in order until successful:

1. **Maven Central POM (authoritative):**
   ```bash
   curl -s "https://repo1.maven.org/maven2/{groupId-as-path}/{artifactId}/{version}/{artifactId}-{version}.pom" \
     | grep -A1 '<url>'
   ```

2. **GitHub releases (most common):**
   - General page: `https://github.com/{org}/{repo}/releases`
   - Verify with fetch_webpage, scan for version-specific entries

3. **Apache projects:**
   - `https://github.com/apache/{project}/blob/master/RELEASE-NOTES.txt`
   - `https://downloads.apache.org/{project}/RELEASE-NOTES.txt`

4. **Spring projects:**
   - `https://github.com/spring-projects/{repo}/releases`

**Generate version-specific URLs:**

Once you find the first working URL, infer the pattern and generate URLs for all versions:

**GitHub patterns:**
- `https://github.com/{org}/{repo}/releases/tag/v{version}` (most common)
- `https://github.com/{org}/{repo}/releases/tag/{version}` (no prefix)
- `https://github.com/{org}/{repo}/releases/tag/r{version}` (JUnit-style)
- `https://github.com/{org}/{repo}/releases/tag/{artifactId}-{version}` (multi-module)

**Example inference:**
```
Found: https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.17
Infer: https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.18
       https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.19
```

**Validation:**
- Verify at least one URL per version (or general page) with fetch_webpage
- If specific version URLs fail, use general releases page
- Mark general pages clearly: "See releases page for version X.Y.Z"

### Step 3: Analyze Changes

**For aggregated changelogs (RELEASE-NOTES.txt, CHANGELOG.md):**
1. Fetch the complete file
2. Search for version markers: "Version X.Y.Z", "## X.Y.Z", "{artifactId} X.Y.Z"
3. Extract content between currentVersion and availableVersion
4. If truncated, note limitation and work with available content

**What to extract (priority order):**

**Required (always include):**
- **CVEs/Security:** All CVE-YYYY-NNNNN with brief impact description, or "None"
- **Breaking Changes:**
  - API removals, signature changes, behavior modifications
  - Module system changes (JPMS, packages)
  - Dependency requirement changes (Java version, library upgrades)
  - Configuration format changes
  - Deprecated features removal

**Include if significant:**
- **Major Features:** Transformative capabilities only (new APIs, major refactors, performance overhauls)
- **Critical Fixes:** Data corruption, security hardening, crash/hang fixes

**Omit:**
- Routine bug fixes and minor enhancements
- Performance tweaks (unless dramatic)
- Documentation and build system updates
- Deprecation warnings (unless removal is imminent)

### Step 4: Return Structured Data

**Format:**
```markdown
## Dependency: {groupId}:{artifactId}
- **Current Version:** {currentVersion}
- **Available Version:** {availableVersion}
- **Release Notes URLs:** 
  - {verified-url-1}
  - {verified-url-2}
  - {verified-url-n}

**CVEs/Security:** {CVE list with impact OR "None"}

**Breaking Changes:**
- {change with version number}
- {change with version number}

**Major Features:**
- {feature with version number}

**Notes:** {migration warnings, stability notes, Java requirements, version gap warnings}
```

**Best practices:**
- List URLs in version order (oldest to newest)
- Prefix changes with version number when gap >2 versions
- Keep descriptions concise (one line per item)
- Note version gaps: "N versions behind (X → Y) - comprehensive review recommended"

## Quality Checklist

Before returning, verify:
1. ✅ Version range is correct (currentVersion exclusive, availableVersion inclusive)
2. ✅ All URLs validated
3. ✅ CVEs/Security section present (even if "None")
4. ✅ Breaking Changes section present (even if empty)
5. ✅ Changes include version numbers (when gap >2)
6. ✅ Data structure matches template exactly
7. ✅ Only significant items included (no routine maintenance)
8. ✅ Notes mention any critical migration concerns
