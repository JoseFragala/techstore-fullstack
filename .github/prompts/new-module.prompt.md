---
mode: agent
description: "Plan and implement the smallest valid TechStore module change while respecting the repository contract and test requirements."
---

# New Module Prompt

Use this flow when implementing or completing a TechStore module.

1. Identify the business resource or feature that needs work.
2. Inspect the current Entity, Repository, DTOs, Mapper, Service, Controller, Exceptions, and tests for the relevant module.
3. Check the relevant rules in docs/04-business-rules.md and docs/06-decisions.md.
4. Identify what already exists and what is missing.
5. Ask for clarification when the requirement is ambiguous.
6. Produce a minimal plan before editing code.
7. Implement only the necessary slice of the feature.
8. Add or update Service and Controller tests when the behavior changes.
9. Run the targeted tests and share the results honestly.
10. Report whether documentation needs updating.

Do not force a full CRUD implementation if the module does not require it.
Do not generate behavior without checking the project rules and existing patterns.
