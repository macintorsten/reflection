---
name: Report-Format-Template
description: Markdown table format for dependency review reports
applyTo: ".github/agents/*dependency*.md"
# Note: applyTo pattern matches same files as maven-dependency-research.instructions.md by design.
# This file guides orchestrators on output format, while maven-dependency-research guides subagents on research methodology.
# Both instructions apply to the maven-dependency-review.agent.md file simultaneously.
---

# Dependency Review Report Format

When generating the final dependency review report, use this exact table structure:

## Important Boundaries

When creating dependency review reports:
- Use the exact table format specified below - do not deviate
- ONE row per dependency group (not per individual dependency)
- Each row must include all required sections (Release Notes, CVEs/Security, Breaking Changes, Notes)
- CVEs/Security section is mandatory (state "None found in release notes" if applicable)
- Keep summaries concise (150-250 words maximum)
- Use proper markdown formatting within table cells

## Markdown Table Format

```markdown
# Maven Dependency Review - {YYYY-MM-DDTHH-MM-SS}

🤖 **Disclaimer:** This report was automatically generated using AI tools. Please verify all findings before making decisions.

## Status
- Total groups: N
- Groups researched: X / N
- Research status: ⏳ In progress / ✅ Complete

## Dependencies to Update

| Dependency | Current Version | Available Version | Summary |
|------------|-----------------|-------------------|---------|
```

**Incremental Updates:** Append new table rows at the end. Update Status section as batches complete.
| `groupId:artifactId` | X.Y.Z | A.B.C | **Release Notes:** [text](url) • [text](url)<br>**CVEs/Security:** {details or "None found in release notes"}<br>**Breaking Changes:**<br>• Item 1<br>• Item 2<br>**Major Features:** (only if significant)<br>• Item 1<br>**Notes:** {migration warnings, stability, requirements} |

## Column Definitions

1. **Dependency** - Format: `groupId:artifactId` wrapped in backticks
2. **Current Version** - Version currently in use (e.g., 2.16.1)
3. **Available Version** - Latest stable version available (e.g., 2.20.1)
4. **Summary** - Structured content with:
   - **Release Notes:** Multiple verified links separated by bullet (•)
   - **CVEs/Security:** Mandatory section (state "None found in release notes" if applicable)
   - **Breaking Changes:** Bullet list of API/behavior changes
   - **Major Features:** Optional, only if transformative
   - **Notes:** Migration complexity, Java requirements, stability warnings, project maintenance status, etc.

## Table Formatting Rules

- Use `<br>` for line breaks within table cells
- Use `<br>•` for bullet points within cells
- Wrap dependency coordinates in backticks for monospace
- Use bold (`**text:**`) for section headers in Summary column
- Separate multiple URLs with bullet character (•) not commas
- Each link format: `[descriptive-text](verified-url)`

## Example Row

```markdown
| `org.apache.commons:commons-lang3` | 3.12.0 | 3.14.0 | **Release Notes:** [v3.14.0](https://github.com/apache/commons-lang/releases/tag/rel/commons-lang-3.14.0) • [Changelog](https://commons.apache.org/proper/commons-lang/changes-report.html)<br>**CVEs/Security:** None found in release notes<br>**Breaking Changes:**<br>• None<br>**Major Features:**<br>• New RandomStringUtils methods for more flexible string generation<br>**Notes:** Minor version upgrade, safe to apply. |
```

## Verification Checklist

When reviewing generated reports, verify:
1. ✅ Table has exactly 4 columns: Dependency | Current Version | Available Version | Summary
2. ✅ Number of table rows matches dependency_groups.jsonl line count
3. ✅ Each row has Release Notes, CVEs/Security, Breaking Changes, and Notes sections
4. ✅ CVEs/Security section is present in every row (stating "None found in release notes" if applicable)
5. ✅ Summaries in the table are concise (150-250 words maximum)
6. ✅ All URLs are formatted correctly and verified, encompassing relevant release notes and changelogs
7. ✅ Markdown formatting uses `<br>` for line breaks and `<br>•` for bullets