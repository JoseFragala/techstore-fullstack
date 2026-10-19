---
applyTo:
  - "src/main/java/com/fragala/techstore/controller/**/*.java"
  - "src/main/java/com/fragala/techstore/dto/**/*.java"
  - "src/main/java/com/fragala/techstore/exception/**/*.java"
description: "Rules for TechStore API, DTO, controller, and HTTP error handling design."
---

# API Instructions

- Controllers must stay thin and focus on HTTP concerns.
- Controllers must not access repositories directly.
- Controllers must not contain business rules or persistence decisions.
- Request and response contracts should use DTOs.
- New request and response DTOs should prefer Java records unless a concrete requirement demands a class.
- Record accessors should use the record API, such as `request.name()`, not `request.getName()`.
- Existing class-based DTOs should not be converted outside the approved scope.
- Request DTOs handle syntactic validation with Jakarta Bean Validation.
- Business validation belongs to the Service or domain logic, not the controller layer.
- JPA entities must not be returned directly by the API.
- HTTP status codes should stay consistent with the established project conventions.
- Errors should be handled consistently through the application exception flow and the global handler.
- API responses must not expose internal details, stack traces, or sensitive data.
