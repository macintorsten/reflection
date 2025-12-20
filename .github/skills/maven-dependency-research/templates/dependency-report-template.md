# Dependency Research Report

**Generated:** {YYYY-MM-DD HH:MM:SS}  
**Researcher:** {Name/Agent ID}

---

## Dependency Information

**GroupId:** `{groupId}`  
**ArtifactId:** `{artifactId}`  
**Current Version:** `{currentVersion}`  
**Available Version:** `{availableVersion}`  
**Versions Researched:** {count} version(s)

---

## Release Notes

List all applicable versions with verified links:

- **{version}**: [Release Notes]({verified-url}) | [Changelog]({verified-url})
- **{version}**: [Release Notes]({verified-url})
- **{version}**: {version} (no direct link found)

**Note:** If ≤5 versions, list every intermediate version even if URLs are identical.

---

## CVEs and Security Issues

{List CVEs and security issues explicitly mentioned in release notes}

**Example:**
- **CVE-2024-12345** (version {version}): {Brief description}
- **Security hardening** (version {version}): {Brief description}

**If none found:** None found in release notes

---

## Breaking Changes

{List API removals, signature changes, behavior modifications, dependency requirement changes}

**Example:**
- **{version}**: Removed deprecated method `{methodName}()` from `{ClassName}`
- **{version}**: Changed return type of `{methodName}()` from `{OldType}` to `{NewType}`
- **{version}**: Minimum Java version increased to {javaVersion}

**If none found:** None

---

## Major Features

{List only transformative capabilities}

**Example:**
- **{version}**: New API for {feature description}
- **{version}**: Significant performance improvement in {area}

**If none significant:** None of significance

---

## Critical Fixes

{List data corruption fixes, crash/hang fixes, critical bug fixes}

**Example:**
- **{version}**: Fixed data corruption issue in {component}
- **{version}**: Fixed memory leak in {component}

---

## Migration Notes

**Effort Estimate:** {Low/Medium/High}  
**Risk Level:** {Low/Medium/High}

**Migration Considerations:**
- {Key consideration 1}
- {Key consideration 2}
- {Key consideration 3}

**Recommended Actions:**
1. {Action 1}
2. {Action 2}
3. {Action 3}

**Java Requirements:** {Java version if changed}  
**Dependency Changes:** {If other dependencies need updating}  
**Configuration Changes:** {If config files need modification}

---

## Project Status

**Maintenance Status:** {Active/Maintenance/Deprecated/Retired}  
**Last Release Date:** {YYYY-MM-DD}  
**Release Cadence:** {Weekly/Monthly/Quarterly/Irregular}

**⚠️ Important Notes:**
- {Any critical project status notes}
- {EOL warnings}
- {Alternative recommendations if retired}

---

## Research Methodology

**Sources Consulted:**
1. {Source 1} - {URL}
2. {Source 2} - {URL}
3. {Source 3} - {URL}

**Version Discovery:**
- Method: {Maven Central XML / Search API / Web Search}
- Total versions found: {count}
- Applicable versions: {count}

**Link Verification:**
- Total links verified: {count}
- Verified successfully: {count}
- Failed verification: {count}

**Research Date:** {YYYY-MM-DD}  
**Research Duration:** {minutes} minutes

---

## Quality Checklist

Before finalizing this report, verify:

- [x] CVEs/Security section present (stated "None found in release notes" if none)
- [x] Breaking Changes section present (even if empty)
- [x] Changes include version numbers (when gap >2)
- [x] Only significant items included (no routine maintenance)
- [x] Notes mention critical migration concerns
- [x] Project retirement/EOL status checked
- [x] For ≤5 versions, all intermediate versions listed in Release Notes
- [x] All URLs verified (200 OK with relevant content)
- [x] Descriptions are concise (one line per item)
- [x] Research methodology documented

---

## Approval

**Ready for Review:** {Yes/No}  
**Reviewer:** {Name}  
**Review Date:** {YYYY-MM-DD}  
**Status:** {Approved/Needs Revision/Rejected}

**Comments:**
{Reviewer comments}
