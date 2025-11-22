---
applyTo: ".github/**/*.md"
---

# GitHub Copilot Authoring Best Practices

These instructions guide AI agents when writing or modifying GitHub Copilot instructions, custom agents, and prompt files.

## Important Boundaries

When creating or modifying Copilot instruction files:
- Follow the file type conventions and naming patterns described below
- Use YAML frontmatter with `applyTo` patterns for instruction files
- Include concrete examples rather than abstract descriptions
- Reference existing files instead of duplicating content
- Keep instructions focused on project-specific information, not generic programming knowledge

## File Types and Purposes

### Custom Instructions (`copilot-instructions.md`)
- **Location:** `.github/copilot-instructions.md`
- **Purpose:** Repository-wide coding standards, build/test commands, and project context
- **Scope:** Applies to all Copilot interactions in this workspace
- **Best for:** Project architecture, tech stack, validation steps, critical reminders

### Custom Agents (`*.agent.md`)
- **Location:** `.github/agents/AGENTNAME.agent.md`
- **Purpose:** Specialized AI teammates for specific tasks
- **Scope:** Invoked explicitly (e.g., `@agentname` in chat)
- **Best for:** Complex workflows requiring orchestration, domain expertise, or multi-step processes

### Instruction Files (`*.instructions.md`)
- **Location:** `.github/instructions/FILENAME.instructions.md`
- **Purpose:** Reusable guidance for specific subtasks
- **Scope:** Applied based on `applyTo` glob patterns in frontmatter, or referenced by agents
- **Best for:** Templates, formats, research procedures, validation checklists

### Prompt Files (`*.prompt.md`)
- **Location:** `.github/prompts/FILENAME.prompt.md`
- **Purpose:** Task-specific, reusable prompts for common workflows
- **Scope:** Available as slash commands in chat (e.g., `/promptname`)
- **Best for:** Code reviews, refactoring patterns, documentation generation

## Custom Agent Structure

### YAML Frontmatter
```yaml
---
name: Agent Name
description: Clear, concise explanation of agent's role and expertise
tools: ['runCommands', 'edit', 'search', 'openSimpleBrowser', 'fetch', 'runSubagent']
---
```

**Key fields:**
- `name`: Unique identifier (defaults to filename if omitted)
- `description`: Explains what the agent does and when to use it
- `tools`: List of tool names the agent can access; omit to allow all tools

### Agent Body (Markdown)

**Structure your agent instructions with:**
1. **Role definition:** "You are an orchestrator for..." or "You are an expert in..."
2. **Process overview:** High-level workflow broken into phases
3. **Detailed steps:** Specific commands, file paths, expected outputs
4. **Boundaries:** What the agent must NOT do
5. **Key principles:** Summary checklist of critical behaviors

**Example:**
```markdown
# Agent Name

You are an expert in [specific domain]. Your role is to [clear purpose].

**Important:** [Critical constraint or boundary]

## Process Overview

### Phase 1: [Phase Name]
1. [Specific action with command]
2. [Expected output or next step]
3. [How to handle edge cases]

### Phase 2: [Phase Name]
[Detailed steps...]

## Key Principles
- **[Principle]:** [Explanation]
- **[Principle]:** [Explanation]
```

## Instruction File Structure

### YAML Frontmatter with applyTo
```yaml
---
applyTo: "**/*.{ts,tsx}"
---
```

**applyTo patterns:**
- Single pattern: `"**/*.ts"`
- Multiple extensions: `"**/*.{ts,tsx}"` (use brace expansion, not comma-separated)
- Specific directory: `"src/**/*.js"`
- All files: `"**/*"`

### Instruction Body (Markdown)

Follow the same principles as agent bodies:
- Clear, specific guidance
- Concrete examples
- Defined boundaries
- Referenced formats or templates

## Writing Effective Instructions

### Be Specific and Concrete
❌ **Avoid:** "Process the dependencies"  
✅ **Better:** "Run `mvn versions:display-dependency-updates -B` and parse output into JSONL"

### Use Examples
Include code samples, expected outputs, and file formats:
```markdown
**Expected output format:**
```json
{"groupId":"org.springframework","artifactId":"spring-core","currentVersion":"5.3.0","availableVersion":"5.3.5"}
```
```

### Set Clear Boundaries
```markdown
**Important boundaries:**
- Never commit secrets or credentials
- Do not modify files in `/vendor` or `/node_modules`
- Only update dependencies with available release notes
```

### Reference Existing Files
Instead of duplicating information, reference other instruction files:
```markdown
Follow the [Dependency Research Instructions](../instructions/dependency-research.instructions.md).
```

### Define Success Criteria
```markdown
**Verification:**
1. Count groups: `wc -l < dependency_groups.jsonl`
2. Count table rows: `grep -c '^\|' report.md`
3. Verify table rows = groups + 2 (header and separator)
```

## Tool Configuration Best Practices

### Restrict Tools by Need
```yaml
# Allow all tools - agent can use any available tool
# (omit tools field or use tools: ['*'])
tools: ['*']

# Better - agent can only read and search
tools: ['read', 'search']

# Best - specific tools for specific tasks
tools: ['runCommands', 'edit', 'search', 'openSimpleBrowser', 'fetch']
```

### Common Tool Combinations
- **Research/Analysis:** `['search', 'fetch', 'openSimpleBrowser']`
- **Code Editing:** `['read', 'edit', 'search']`
- **Build/Test:** `['runCommands', 'read']`
- **Orchestration:** `['runCommands', 'edit', 'search', 'runSubagent']`

## Delegation and Subagents

### When to Use Subagents
- Task requires specialized domain knowledge
- Subtask is clearly scoped and repeatable
- Want to maintain separation of concerns

### How to Delegate
Provide a detailed prompt to the subagent:
```markdown
Launch #tool:runSubagent with this prompt:

Research Maven dependency updates for the following group:
{group_json}

Return findings with verified release notes and security information.
```

### Best Practices
- Keep subagent responsibilities tightly scoped
- Provide complete context in the prompt
- Specify expected output format
- Allow flexibility in execution strategy (avoid over-constraining)

## Incremental Progress Updates

When orchestrating multi-step workflows:
1. Create initial structure/report at the START
2. Update after each meaningful unit of work
3. Make progress visible throughout execution

```markdown
**Initial Report Structure:**
Create file with header and empty sections:
```markdown
# Report Title - {timestamp}

## Section 1
(Content will be added incrementally)

## Status
- Work in progress...
```

**Incremental Updates:**
After each subtask:
1. Parse results
2. Format according to template
3. Insert into report
4. Continue to next task
```

## What NOT to Include

GitHub Copilot has built-in knowledge of:
- Common programming languages, frameworks, and libraries
- Standard development tools (git, npm, maven, etc.)
- General software engineering principles
- Code review best practices

**Don't include:**
- Tutorials on how to use git, npm, etc.
- Language syntax references
- Generic best practices unrelated to your project
- Information readily available in official documentation

**Do include:**
- Project-specific conventions and standards
- Custom tooling and commands
- Repository structure and file organization
- Domain-specific terminology and concepts
- Integration points between systems
- Edge cases and gotchas specific to your codebase

## Maintenance Tips

### Keep Instructions Current
- Update when tech stack changes
- Revise after discovering edge cases
- Remove obsolete information promptly

### Test Your Instructions
- Have team members try using the agents
- Verify commands actually work as written
- Check that referenced files exist

### Modular Organization
- Break complex instructions into smaller, focused files
- Use clear naming conventions
- Reference related instructions to avoid duplication

### Version Control
- Commit instruction changes with descriptive messages
- Review instruction changes like code changes
- Document breaking changes to agent behavior

## Example: Good vs. Bad Instructions

### ❌ Bad Example
```markdown
# My Agent
You are a helpful agent. Do the work.
```
**Problems:** Vague role, no process, no boundaries

### ✅ Good Example
```markdown
---
name: Dependency Reviewer
description: Reviews Maven dependencies for security and breaking changes
tools: ['runCommands', 'search', 'fetch', 'edit']
---

# Dependency Reviewer

You review Maven dependency updates, prioritizing security and breaking changes.

## Process
1. Run `mvn versions:display-dependency-updates` to identify updates
2. For each dependency:
   - Search for release notes
   - Check for CVEs
   - Document breaking changes
3. Generate report in `.github/dependency-review-{timestamp}.md`

## Boundaries
- Only suggest updates with verified release notes
- Never auto-update without user approval
- Skip alpha/beta/RC versions

## Output Format
Follow [Report Format Template](../instructions/report-format.instructions.md)
```

## Resources

- [GitHub Custom Agents Documentation](https://docs.github.com/en/copilot/concepts/agents/coding-agent/about-custom-agents)
- [VS Code Copilot Customization](https://code.visualstudio.com/docs/copilot/customization/custom-instructions)
- [How to Write a Great agents.md](https://github.blog/ai-and-ml/github-copilot/how-to-write-a-great-agents-md-lessons-from-over-2500-repositories/)

## Verification Checklist

When reviewing instruction files (including this one), verify:
1. ✅ YAML frontmatter with appropriate `applyTo` glob pattern
2. ✅ Clear boundaries section near the top
3. ✅ Concrete examples for each guideline (not just abstract rules)
4. ✅ Specific commands, file paths, or patterns (not vague descriptions)
5. ✅ References to other files instead of duplicated content
6. ✅ Focus on project-specific information (not generic programming knowledge)
7. ✅ Success criteria or verification checklist at the end
