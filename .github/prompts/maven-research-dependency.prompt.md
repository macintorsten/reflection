---
name: maven-research-dependency
description: Research Maven dependency updates with CVEs, breaking changes, and verified release notes
argument-hint: "one or more dependencies to research"
tools: ['runCommands', 'search', 'fetch']
---

# Research Maven Dependencies

Analyze Maven dependency updates to identify security issues, breaking changes, and major features.

**You can research:**
- Single dependency or multiple dependencies (recommended: 3-5 at a time)
- Accept any format: JSON, shorthand notation, or natural language

**For each dependency, provide:**
- Verified release notes URLs
- CVEs/Security analysis (mandatory, state "None" if empty)
- Breaking changes with version numbers
- Major features (only if transformative)
- Migration notes and warnings

Follow the [Maven Dependency Research Skill](../skills/maven-dependency-research/SKILL.md) for detailed methodology.
