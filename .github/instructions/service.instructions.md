---
applyTo:
  - "src/main/java/com/fragala/techstore/service/**/*.java"
description: "Rules for TechStore service-layer implementation and business logic."
---

# Service Instructions

- Services implement use cases and business rules.
- Services coordinate repositories and mappers.
- Prefer constructor injection.
- Services must not return `ResponseEntity`.
- Services must not depend on controllers.
- Resolve related entity IDs before creating or updating the aggregate.
- Throw specific application exceptions for expected failures.
- Keep business rules out of Controllers and Repositories.
- Use `@Transactional` only when multiple related changes must be treated as one unit.
- Do not add transactions without a concrete need.
- Preserve historical integrity when deleting or deactivating records that remain referenced.
- Use `BigDecimal` for all monetary values.
- Do not invent business rules when the requirement is incomplete or ambiguous.
