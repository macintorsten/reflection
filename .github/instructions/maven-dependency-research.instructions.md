````instructions
---
name: Maven-Dependency-Research-Instructions
description: Instructions for subagents researching Maven dependency updates
---

# Maven Dependency Research Instructions

You are a research subagent analyzing Maven dependency updates. Your task is to investigate one or more dependencies and provide structured information about available updates.

Create a plan with a corresponding todo-list for the process described below and then execute it.

## Execution Strategy

### Performance Guidelines
- **Minimize network calls:** Check local reports first, then make single efficient API calls.
- **Validate commands immediately:** Ensure placeholders are replaced and commands return data.
- **Batch related work:** When fetching GitHub releases, get all versions in one call.
- **Fail fast:** If a strategy doesn't work, move to the next immediately.

### Multiple Dependency Groups
If the task requires generating reviews for multiple dependency groups, you must repeat the **Single Group Research Workflow** (defined below) independently for each group. Complete the full workflow for one group before starting the next.

## Single Group Research Workflow

Follow these five phases in order to research a single `dependencyGroup`.

### Phase 1: Identification
Identify the defining attributes of the dependency group. All artifacts in a group share the same GroupId and Available Version, allowing them to be researched together.
- **GroupId**: The common group identifier.
- **ArtifactIds**: The list of artifacts.
- **Current Version**: The version currently in use.
- **Available Version**: The target version.

### Phase 2: Version Scope Definition
Identify all versions that need to be reviewed.

1. **Fetch Metadata**: 
   - Use `curl -sL --fail --retry 2 --max-time 5 https://repo1.maven.org/maven2/{group-as-path}/{artifactId-as-path}/maven-metadata.xml | grep -C 10 "{currentVersion}\|{availableVersion}"` to identify available versions. This XML often contains ALL published versions - it is the authoritative source. Replace placeholders with actual values.
   - If previous command fails, use `curl -sL --fail --retry 2 --max-time 5 "https://search.maven.org/solrsearch/select?q=g:{groupId}+AND+a:{artifactId}&rows=100&wt=json" | jq -r '.response.docs[].v' | grep -C 10 "{currentVersion}\|{availableVersion}"`
   - If both commands fail, use web search to find an alternative source for version history.
   - **CRITICAL:** Replace all placeholder text `{...}` with actual values before running commands. Test commands return data before proceeding.
2. **Filter Versions**: Extract the **complete** list of all published versions and filter for **Applicable Versions**:
   - Strictly greater than `Current Version`.
   - Less than or equal to `Available Version`.
   - **Exclude** pre-release versions (alpha, beta, RC).

**Approach:**
- ≤5 versions: Research each version individually. Ensure every intermediate version is identified and listed in your final report.
- >5 versions: Aggregate findings, note "N versions behind - comprehensive review recommended".

### Phase 3: Release Notes Discovery & Verification
Find and verify the documentation describing changes for the Applicable Versions.

**Goal:** For each Applicable Version identified in Phase 2, find a specific release note URL.

**Strategies (in priority order):**

1. **Check previous reports (fastest):**
   ```bash
   grep -h "{groupId}" dependency-review-*.md 2>/dev/null | grep -o 'https://[^)]*' | sort -u
   ```

2. **Query Maven Central POM (fast, single request):**
   Fetch POM to find `<url>` or `<scm>` tags pointing to project homepage/repository.
   ```bash
   curl -sL --fail --retry 2 --max-time 5 "https://repo1.maven.org/maven2/{groupId-as-path}/{artifactId}/{availableVersion}/{artifactId}-{availableVersion}.pom" \
     | grep -E "<url>|<connection>" -A 1 | grep -oP '(https?://[^<]+)'
   ```
   **Replace placeholders** `{groupId-as-path}`, `{artifactId}`, `{availableVersion}` with actual values. Use forward slashes for groupId path (e.g., `com/google/guava`).

3. **GitHub Releases API (if GitHub URL found):**
   If Strategy 2 reveals a GitHub URL (`github.com/{org}/{repo}`), fetch ALL releases at once:
   ```bash
   curl -sL --fail --retry 2 --max-time 5 "https://api.github.com/repos/{org}/{repo}/releases?per_page=100" \
     | jq -r '.[] | "\(.tag_name)|\(.html_url)"' | grep -E "v?{version-pattern}"
   ```
   This returns tag and URL pairs. Filter for relevant versions using grep pattern.

4. **Web search (slowest, last resort):** "{artifactId} release notes" or "{artifactId} changelog".

**Recursive Discovery & Verification:**
- **Recursive Discovery:** If a general page is found (e.g., a list of releases), fetch it. If it contains links to specific version details (e.g., 'Read more', 'Full Changelog'), follow and verify those links.
- **Verification:** You MUST use `fetch_webpage` (or equivalent tool e.g. curl) to verify content relevance, not just HTTP status. A link is valid if it returns 200 OK AND contains relevant release information for the specific version.

**Single Page Release Notes:**
- If a single page contains notes for multiple versions, you MUST attempt to find anchors (e.g., `#v1.2.0`) for each specific version. If anchors are not found, use the same base URL for all versions.

**Command Validation:**
- Always replace `{...}` placeholders with actual values before execution.
- Verify commands return expected data (versions, URLs) before proceeding to next phase.
- If a command returns empty output or errors, try the next strategy immediately.
- **DO NOT** retry failed commands with minor variations - move to next strategy.

**Note:** If official documentation requires authentication (e.g., Oracle), use web search to find alternative sources.

### Phase 4: Content Extraction
Analyze the content found in Phase 3 (from the *verified* links) for *all* Applicable Versions identified in Phase 2.

**What to extract:**
- **Security:** Only include CVEs or security issues explicitly mentioned in the release notes or project pages you encounter. Do not actively search for CVEs or security databases. If a CVE ID is found without details, report the ID only; do not research further.
- **Breaking Changes:** API removals, signature changes, behavior modifications, dependency requirement changes.
- **Major Features:** Transformative capabilities only.
- **Critical Fixes:** Data corruption, security hardening, crash/hang fixes.

**Omit:** Routine bug fixes, minor enhancements, performance tweaks, documentation updates.

### Phase 5: Reporting
Compile the findings into the final output using the format below.

```markdown
## Dependency: {groupId}:{artifactId}
**Current Version:** {currentVersion}  
**Available Version:** {availableVersion}  
**Release Notes:** [General release notes](url) • [Version specific release notes](url)
**CVEs/Security:** {details found in release notes or "None found in release notes"}  
**Breaking Changes:**
- {version}: {change description}
- {version}: {change description}

**Major Features:**
- {version}: {feature description}

**Notes:** {migration warnings, stability notes, Java requirements, retirement status}
```

**Requirements:**
- Always include CVEs/Security and Breaking Changes sections. For Security, state "None found in release notes" if no mentions were found.
- Keep descriptions concise (one line per item).
- Prefix changes/features with version number when gap >2 versions.
- **Release Notes:** If ≤5 versions, you MUST list EVERY intermediate version individually, even if they share the same URL. Example: `[1.0.1](url) • [1.0.2](url)`. If a link was found but failed verification (no relevant content), do not include it. Format: `[1.2.0](url)` if found, otherwise `1.2.0 (no direct link found)`. Separate with ` • `.
- **Completeness Check:** Ensure no intermediate versions are skipped in the Release Notes list.
- **Link Verification:** All URLs in the report MUST be verified (return 200 OK).
- List release note URLs in version order.
- **If project is retired:** Include "⚠️ Project retired, consider migrating to [alternative]" in Notes.

## Quality Checklist

Before returning, verify:
1. ✅ CVEs/Security section present for each dependency (stating "None found in release notes" if applicable)
2. ✅ Breaking Changes section present for each dependency (even if empty)
3. ✅ Changes include version numbers (when gap >2)
4. ✅ Only significant items included (no routine maintenance)
5. ✅ Notes mention any critical migration concerns
6. ✅ Project retirement/EOL status checked and noted if applicable
7. ✅ All requested dependencies have been researched
8. ✅ All commands executed successfully with proper placeholder substitution
9. ✅ For ≤5 versions, every intermediate version is listed in Release Notes (even if URLs are identical).
10. ✅ All links in report verified (fetched and confirmed relevant content).

````
