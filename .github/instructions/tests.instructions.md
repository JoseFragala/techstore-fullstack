---
applyTo:
  - "src/test/**/*.java"
description: "Rules for TechStore automated test design and execution."
---

# Test Instructions

- Use JUnit 5 for automated tests.
- Use Mockito in unit tests where a Spring context is not required.
- Use MockMvc for controller tests.
- Prefer `@ExtendWith(MockitoExtension.class)` in unit tests.
- Do not load the Spring context when it is not necessary.
- Name tests using the pattern `method_shouldExpectedResult_whenCondition`.
- Cover both the positive path and the most relevant failure cases.
- Verify that persistence is not called after rejected operations.
- Do not remove or weaken tests just to make the build pass.
- Do not test Lombok-generated getters and setters.
- Avoid `Thread.sleep`.
- Use `BigDecimal` in monetary assertions.
- Keep tests deterministic and independent.
- Update test access to `recordComponent()` when a DTO is converted to a record.
