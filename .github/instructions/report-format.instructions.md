---
name: Report Format Template
description: Markdown table format for dependency review reports
---

# Dependency Review Report Format

When generating the final dependency review report, use this exact table structure:

## Markdown Table Format

```markdown
# Maven Dependency Review - {YYYY-MM-DDTHH-MM-SS}

## Dependencies to Update

| Dependency | Current Version | Available Version | Summary |
|------------|-----------------|-------------------|---------|
| `groupId:artifactId` | X.Y.Z | A.B.C | **Release Notes:** [text](url) • [text](url)<br><br>**CVEs/Security:** {details or "None"}<br><br>**Breaking Changes:**<br>• Item 1<br>• Item 2<br><br>**Major Features:** (only if significant)<br>• Item 1<br><br>**Notes:** {migration warnings, stability, requirements} |

## Verification Status
- Total dependencies: N
- All URLs verified: ✅
- All version ranges confirmed: ✅
```

## Column Definitions

1. **Dependency** - Format: `groupId:artifactId` wrapped in backticks
2. **Current Version** - Version currently in use (e.g., 2.16.1)
3. **Available Version** - Latest stable version available (e.g., 2.20.1)
4. **Summary** - Structured content with:
   - **Release Notes:** Multiple verified links separated by bullet (•)
   - **CVEs/Security:** Mandatory section (even if "None")
   - **Breaking Changes:** Bullet list of API/behavior changes
   - **Major Features:** Optional, only if transformative
   - **Notes:** Migration complexity, Java requirements, stability warnings

## Table Formatting Rules

- Use `<br><br>` for line breaks within table cells
- Use `<br>•` for bullet points within cells
- Wrap dependency coordinates in backticks for monospace
- Use bold (`**text:**`) for section headers in Summary column
- Separate multiple URLs with bullet character (•) not commas
- Each link format: `[descriptive-text](verified-url)`

## Example Row

```markdown
| `org.apache.commons:commons-lang3` | 3.12.0 | 3.14.0 | **Release Notes:** [v3.14.0](https://github.com/apache/commons-lang/releases/tag/rel/commons-lang-3.14.0) • [Changelog](https://commons.apache.org/proper/commons-lang/changes-report.html)<br><br>**CVEs/Security:** None<br><br>**Breaking Changes:**<br>• None<br><br>**Major Features:**<br>• New RandomStringUtils methods for more flexible string generation<br><br>**Notes:** Minor version upgrade, safe to apply. |
```
