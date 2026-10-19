---
mode: agent
description: "Review a local change set for correctness, scope compliance, and regression risk in the TechStore repository."
---

# Review Diff Prompt

Review the current change set using the repository rules.

1. Read `git diff` and `git diff --staged`.
2. Read AGENTS.md and the relevant path-specific instructions for modified files.
3. Check whether the change is within scope.
4. Review architecture, validation, HTTP behavior, historical integrity, monetary handling, and persistence rules.
5. Identify regressions or contract violations.
6. Run the relevant tests when possible.
7. Produce findings classified as:
   - Blocker
   - Important
   - Recommendation
8. Include a suggested PR description summary.

Do not invent findings. Do not treat style preference as a functional defect.
