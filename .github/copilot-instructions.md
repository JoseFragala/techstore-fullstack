---
applyTo: "**/*"
description: "Operational rules for Copilot when working in the TechStore repository."
---

# Copilot Instructions

- Read AGENTS.md before starting any task.
- Treat the current source code as the source of truth; inspect similar modules before creating new code.
- When a business rule is ambiguous, stop and ask for clarification before changing persistence or behavior.
- Stay within the approved scope. Avoid opportunistic refactors or broad redesigns.
- Changes that alter behavior must include or update tests.
- Report test results honestly. Do not claim a test passed without executing it.
- Answer the project owner in Brazilian Portuguese.
- Keep project code, classes, files, and names in English.
- Do not commit, push, or deploy without explicit approval.
- Finish with a concise summary covering changed files, tests, assumptions, and pending items.
