---
applyTo: ".github/**/*.md"
---

# GitHub Copilot Authoring Instructions

## File Types
| Type | Pattern | Purpose |
|---|---|---|
| **Custom Instructions** | `.github/copilot-instructions.md` | Repo-wide context, standards, build/test commands. |
| **Instructions** | `.github/instructions/*.instructions.md` | Reusable guidance, templates, checklists. |
| **Prompts** | `.github/prompts/*.prompt.md` | Task-specific prompts (slash commands). |

## Structure & Templates

### Instruction File (`.instructions.md`)
```yaml
---
applyTo: "**/*.{ts,tsx}" # Glob pattern
---
```
(Follows same markdown structure as Agent Body)

## Authoring Rules
- **Specifics:** Use concrete commands/paths (e.g., `mvn clean package`) over vague descriptions.
- **Examples:** Include code samples and expected output formats.
- **Boundaries:** Explicitly state what NOT to do (e.g., "Never commit secrets").
- **References:** Link to existing instruction files to avoid duplication.
- **Scope:** Focus on project-specifics (conventions, tooling), not generic knowledge.

## Tool Configuration
- **Research:** `['search', 'fetch', 'openSimpleBrowser']`
- **Editing:** `['read', 'edit', 'search']`
- **Build/Test:** `['runCommands', 'read']`

## Incremental Updates
For multi-step workflows, create an initial report and update it incrementally after each step.

## Verification Checklist
1. ✅ YAML frontmatter correct (`applyTo` or `tools`).
2. ✅ Clear boundaries defined.
3. ✅ Concrete commands and examples used.
4. ✅ Project-specific focus.
5. ✅ Success criteria included.
