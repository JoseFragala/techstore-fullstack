---
mode: agent
description: "Check whether the current TechStore documentation still matches the implementation and update only the impacted documents."
---

# Update Documentation Prompt

Review the current code and compare it with the project documentation.

Check these files:

- README.MD
- docs/01-project-overview.md
- docs/02-architecture.md
- docs/03-domain-model.md
- docs/04-business-rules.md
- docs/05-roadmap.md
- docs/06-decisions.md
- docs/PROJECT_CONTEXT.md

Then:

1. Identify statements that are now inaccurate or incomplete.
2. Update only the documents that are affected.
3. Keep the changes tight and specific.
4. Do not rewrite unrelated sections.
5. Do not mark behavior as complete without code and test evidence.
6. Record new architectural decisions with context, decision, justification, and consequences.

Avoid broad documentation churn. Prefer precise updates that reflect observed implementation reality.
