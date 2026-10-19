---
applyTo:
  - "src/main/java/com/fragala/techstore/entity/**/*.java"
  - "src/main/java/com/fragala/techstore/repository/**/*.java"
  - "src/main/java/com/fragala/techstore/mapper/**/*.java"
description: "Rules for TechStore persistence, entity modeling, and mapping behavior."
---

# Persistence Instructions

- Entities represent the persistent business model.
- Repositories provide persistence access, not business rules.
- Mappers are classes and not records.
- Mappers perform conversion work and do not store state.
- Mappers do not access repositories.
- Mappers do not implement business logic.
- Monetary values must use `BigDecimal`.
- Relationships should use object references instead of primitive foreign-key fields.
- Do not add `CascadeType.ALL` unless the parent owns the full lifecycle of the child.
- Do not add `orphanRemoval` without a clear lifecycle dependency.
- Do not change fetch strategies without a concrete query or performance problem and a clear explanation of the consequences.
- Preserve decisions documented in docs/06-decisions.md.
- Do not create repositories or queries without a real use case.
