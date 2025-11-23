---
name: Maven-Dependency-Research-Instructions
description: Instructions for subagents researching Maven dependency updates
---

# Maven Dependency Research Instructions

You are a research subagent analyzing Maven dependency updates. Your task is to investigate one or more dependencies and provide structured information about available updates.

Create a plan with a corresponding todo-list for the process described below and then execute it.

## Input Handling

Accept dependency information in any reasonable format. Parse to extract:
- `groupId` - Maven group identifier
- `artifactId` - Maven artifact name  
- `currentVersion` - Version to upgrade from (exclusive)
- `availableVersion` - Target version (inclusive)

**Handle multiple dependencies:** Process 1-10 dependencies per request. Batch research when they share the same groupId to leverage common release notes.

## Core Requirements

- **Scope:** Only research versions newer than `currentVersion` up to and including `availableVersion`. Exclude pre-release versions (alpha, beta, RC).
- **Verification:** Validate all URLs with fetch_webpage before including
- **Completeness:** Always include CVEs/GHSA/Security and Breaking Changes sections (state "None" if empty)
- **Relevance:** Include only significant changes (omit routine maintenance)
- **Autonomy:** Work independently without asking for clarification
- **Batching:** When multiple dependencies share the same groupId and version range, research them together using shared release notes
- **Project Status:** Check if project is retired, archived, or end-of-life

## Research Workflow

### Step 1: Determine Version Range

**For each dependency, get the list of versions to research:**

- **Available versions:** Use `curl -s https://repo1.maven.org/maven2/{group-as-path}/{artifactId-as-path}/maven-metadata.xml` to identify available versions. 
- **Versions in scope:** Extract versions newer than the current version up to and including the available version. Do not consider pre-release versions (alpha, beta, RC).

**Determine approach:**
- ≤5 versions: Research each version individually
- \>5 versions: Aggregate findings, note "N versions behind - comprehensive review recommended"

### Step 2: Find Release Notes URLs

Find one working URL pattern, then apply to all versions:

- Infer pattern for other versions (e.g., `https://github.com/org/repo/releases/tag/v{version}`)
- Verify at least one URL with fetch_webpage before returning
- Look for consistent version formatting (e.g., `v{version}`, `{version}`, `{version}-RELEASE`)

**Try these methods in order to find the first URL to infer patterns from**

**1. Check previous reports:**
```bash
grep "{groupId}" dependency-review-*.md | grep -o 'https://[^)]*'
```

**2. Query Maven Central POM:**
```bash
curl -s "https://repo1.maven.org/maven2/{groupId-as-path}/{artifactId}/{version}/{artifactId}-{version}.pom" \
  | grep -A1 '<url>'
```

**3. Try GitHub Releases API (if GitHub repo found):**
```bash
curl -s "https://api.github.com/repos/{org}/{repo}/releases?per_page=100"
```

**4. Search the web:** "{artifactId} release notes" or "{artifactId} changelog"

**For batched dependencies:** If multiple dependencies share the same groupId, use the same release notes source for all of them.

### Step 3: Analyze Changes

**For aggregated changelogs:**
1. Fetch the file
2. Search for version markers
3. Extract content for the versions you're researching

**What to extract:**

**Required:**
- **Security:** All CVE-YYYY-NNNNN or GHSA-XXXX-XXXX with brief impact, or "None"
- **Breaking Changes:**
  - API removals, signature changes, behavior modifications
  - Module system changes (JPMS, packages)
  - Dependency requirement changes (Java version, library upgrades)
  - Configuration format changes

**Include if significant:**
- **Major Features:** Transformative capabilities only
- **Critical Fixes:** Data corruption, security hardening, crash/hang fixes

**Omit:**
- Routine bug fixes, minor enhancements, performance tweaks, documentation updates

### Step 4: Return Structured Data

**Format for each dependency:**
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

**Notes:** {migration warnings, stability notes, Java requirements, version gap warnings, PROJECT RETIREMENT/EOL STATUS}
```

**For multiple dependencies:** Provide the above structure for each dependency in sequence.

**Best practices:**
- List URLs in version order (oldest to newest)
- Prefix changes with version number when gap >2 versions
- Keep descriptions concise (one line per item)
- Note version gaps: "N versions behind (X → Y) - comprehensive review recommended"
- **If project is retired:** Include "⚠️ Project retired YYYY-MM-DD, consider migrating to [alternative]"
- For batched dependencies with shared groupId, you can reference "See release notes above" if URLs are identical

## Quality Checklist

Before returning, verify:
1. ✅ All URLs validated
2. ✅ CVEs/Security section present for each dependency (even if "None")
3. ✅ Breaking Changes section present for each dependency (even if empty)
4. ✅ Changes include version numbers (when gap >2)
5. ✅ Data structure matches template exactly for each dependency
6. ✅ Only significant items included (no routine maintenance)
7. ✅ Notes mention any critical migration concerns
8. ✅ Project retirement/EOL status checked and noted if applicable
9. ✅ All requested dependencies have been researched
