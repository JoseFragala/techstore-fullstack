---
name: module-development
description: "Use this skill when creating or completing a TechStore module, CRUD slice, service/controller workflow, or other feature-focused implementation task."
---

# Module Development Skill

## When to use

Use this skill when the task involves:

- creating a new module
- completing CRUD behavior
- creating or completing endpoints
- implementing Service and Controller logic
- continuing modules such as User, Cart, or Order
- implementing a vertical slice of functionality

## Procedure

1. Read AGENTS.md before any implementation.
2. Identify the actual status of the target module in the current codebase.
3. Review the relevant documentation and a similar existing module.
4. Identify rules, dependencies, and constraints before planning the change.
5. Keep the slice as small as possible and avoid starting unrelated layers.
6. Implement the required behavior without creating unnecessary abstractions.
7. Use records for new DTOs when applicable; do not convert legacy DTOs outside the approved scope.
8. Update related accessors, mappers, services, and tests when converting DTOs.
9. Test Service and Controller behavior when relevant.
10. Run the relevant tests and report the outcome honestly.
11. Review the diff for accidental scope drift.
12. Report whether documentation needs updates.

## Constraints

- Use the project’s current architecture.
- Prefer existing implementation patterns.
- Do not invent business rules.
- Do not perform opportunistic refactors.
- Do not broaden the task beyond the approved behavior.
