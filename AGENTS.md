# TechStore — Agent Contract

TechStore is a full-stack e-commerce project created to study professional software development with Java, Spring Boot, PostgreSQL, testing, and modern frontend technologies.

This file is the main source of truth for AI agents working in this repository.

Before planning, reviewing, or changing code, read this file and inspect the existing implementation related to the requested task.

---

## 1. Project Purpose

TechStore is a learning and portfolio project that models an e-commerce system.

The project is intended to:

- strengthen Java and Spring Boot knowledge;
- practice object-oriented programming;
- model a relational database with PostgreSQL;
- build REST APIs using a layered architecture;
- apply validation and exception handling;
- develop automated tests;
- practice Git and GitHub workflows;
- progressively introduce authentication, containers, CI/CD, cloud deployment, and frontend development.

The code should be understandable to a developer who is still learning.

Prefer clear implementations over unnecessary abstractions.

Do not introduce enterprise-level complexity unless the current requirement justifies it.

---

## 2. Communication and Language

- Communicate with the project owner in Brazilian Portuguese unless explicitly asked to use another language.
- Source code must be written in English.
- Class, method, variable, package, endpoint, field, test, and file names must be written in English.
- Code comments and Javadocs must be written in English.
- Documentation files should be written in English unless the user explicitly requests otherwise.
- Explain important decisions in clear and educational language.
- Do not hide relevant reasoning behind vague statements such as "best practice".
- When recommending a pattern, explain what concrete problem it solves in this project.

---

## 3. Current Technology Stack

Use the technologies already present in the repository.

### Backend

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Hibernate
- Jakarta Bean Validation
- Lombok
- Maven
- Maven Wrapper

### Database

- PostgreSQL 17
- Docker Compose for the local PostgreSQL instance

### Testing

- JUnit 5
- Mockito
- Spring MVC Test
- MockMvc

### Planned Technologies

The following technologies may be documented as future goals but must not be treated as already implemented:

- Spring Security
- JWT
- React
- Fresh
- Tailwind CSS
- Flyway or Liquibase
- Testcontainers
- Azure deployment
- CI/CD
- payment integration

Do not create code that depends on a planned technology unless the current task explicitly introduces that technology.

---

## 4. Current Project Architecture

TechStore currently follows a layered architecture:


HTTP request
    |
    v
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
PostgreSQL

---

This project does not currently implement Clean Architecture, Hexagonal Architecture, or a completely framework-independent domain layer.

Do not describe or redesign the project as one of those architectures unless an explicit architectural change has been approved.

## 5. Package Responsibilities

### controller

## Location:

src/main/java/com/fragala/techstore/controller

## Responsibilities:

receive HTTP requests;
validate request bodies with @Valid;
receive route parameters;
call the appropriate service;
return the correct HTTP response;
use request and response DTOs.

## Controllers must not:

access repositories directly;
contain business rules;
manipulate JPA entities;
decide persistence behavior;
duplicate service validations;
return JPA entities in the public API.

---

### service

## Location:

src/main/java/com/fragala/techstore/service

## Responsibilities:

implement application use cases;
coordinate repositories and mappers;
resolve referenced entities;
enforce business rules;
decide whether an entity can be created, updated, deleted, activated, or deactivated;
throw meaningful application exceptions;
define transaction boundaries when necessary.

## Services must not:

contain HTTP-specific behavior;
return ResponseEntity;
depend on controllers;
silently ignore invalid business operations;
expose JPA entities to controllers when a response DTO exists.

### repository

## Location:

src/main/java/com/fragala/techstore/repository

## Responsibilities:

provide persistence operations;
extend Spring Data repository interfaces;
declare queries required by application use cases;
answer persistence questions such as existence or references.

## Repositories must not:

implement business rules;
return HTTP responses;
map entities to DTOs;
contain controller behavior;
be created when no application use case requires them.

Prefer Spring Data derived queries when their names remain clear.

Use explicit queries only when a derived query would become unclear or insufficient.

### entity

## Location:

src/main/java/com/fragala/techstore/entity

## Responsibilities:

represent the persistent business model;
define JPA mappings and relationships;
protect important state transitions;
expose domain methods when a change represents business intent.

Examples of domain behavior include:

product.activate();
product.deactivate();
user.changeEmail(newEmail);
cart.addItem(product, quantity);

Avoid exposing unrestricted setters for fields that represent important business state.

Do not place HTTP, controller, or DTO behavior inside entities.


### `dto.request`

Location:

```text
src/main/java/com/fragala/techstore/dto/request
```

Responsibilities:

- represent data accepted by the API;
- contain syntactic input validation;
- expose only fields clients are allowed to send.

#### DTO representation

New request DTOs should be implemented as Java records unless a concrete
framework or behavioral requirement requires a regular class.

Records are preferred because API request objects represent immutable data
received at the application boundary.

Example:

```java
public record CreateCategoryRequest(
    @NotBlank String name
) {
}
```

Validation annotations must be declared directly on the record components.

Existing class-based DTOs do not need to be converted as an unrelated side
effect of another task. Convert them only as part of an explicit refactoring
or when the affected module is already within the approved scope.

Examples of syntactic validation:

- blank fields;
- malformed email;
- invalid size;
- negative values;
- missing required fields.

Request DTOs must not:

- expose JPA entities;
- contain repositories;
- execute business workflows;
- accept server-controlled fields without an explicit reason;
- be mutable without a concrete framework or behavioral requirement.

When a request references an existing resource, prefer its identifier.

Example:

```java
public record CreateProductRequest(
    @NotBlank String name,
    @NotNull Long brandId,
    @NotNull Long categoryId
) {
}
```

Do not receive complete `Brand` or `Category` entities from the client.

---

### `dto.response`

Location:

```text
src/main/java/com/fragala/techstore/dto/response
```

Responsibilities:

- define the public API response;
- expose only data intended for the client;
- prevent persistence details from leaking through the API.

#### DTO representation

New response DTOs should be implemented as Java records unless a concrete
framework or behavioral requirement requires a regular class.

Response DTOs represent immutable API output and should not expose setters.

Example:

```java
public record CategoryResponse(
    Long id,
    String name
) {
}
```

When using a record, access its values through record accessors:

```java
response.name();
```

Do not use JavaBean-style getters that do not exist in a record:

```java
response.getName();
```

Existing class-based response DTOs do not need to be converted as an unrelated
side effect of another task. Convert them only as part of an explicit
refactoring or when the affected module is already within the approved scope.

Response DTOs must not:

- expose sensitive data;
- expose persistence implementation details;
- contain repositories or services;
- execute business workflows;
- expose setters without a concrete framework or behavioral requirement.

Do not return JPA entities directly when a response DTO exists.

### mapper

## Location:

src/main/java/com/fragala/techstore/mapper

## Responsibilities:

convert entities to response DTOs;
convert request DTOs to new entities when appropriate;
apply request data to an existing entity when appropriate.

## Mappers must not:

access repositories;
decide whether a business operation is allowed;
throw HTTP exceptions;
silently perform database operations;
replace service-level business validation.

When a request references another entity by ID, the service resolves that entity before calling the mapper.

### exception

## Location:

src/main/java/com/fragala/techstore/exception

##  Responsibilities:

represent expected application failures;
translate exceptions into consistent HTTP responses through GlobalExceptionHandler;
avoid exposing stack traces or infrastructure details to API clients.

Use ResourceNotFoundException when a requested resource does not exist.

Use specific exceptions for meaningful business conflicts when they improve clarity, such as:

CategoryAlreadyExistsException
BrandAlreadyExistsException
ProductAlreadyExistsException

Do not throw generic RuntimeException when an existing specific exception represents the problem.

### config

## Location:

src/main/java/com/fragala/techstore/config

## Responsibilities:

hold application configuration;
initialize mandatory system data;
define infrastructure-related application behavior.

Configuration components must not become substitutes for services or business workflows.

Sample business data must not be mixed with mandatory system configuration.

## 6. Dependency Direction

### The expected dependency flow is:

controller -> service -> repository
                    |
                    v
                  mapper

Entities and DTOs are used by the appropriate layers.

The following dependencies are prohibited:

repository -> service
repository -> controller
service -> controller
entity -> controller
entity -> repository
mapper -> repository

A controller must never access a repository directly.

If a requested implementation requires breaking the dependency direction, stop and explain the architectural conflict before changing code.

## 7. Existing Domain Rules

Preserve the business decisions already documented in docs/06-decisions.md.

### General rules
Entity relationships use object references instead of primitive foreign-key fields.
Cart and Order are separate business concepts.
CartItem and OrderItem represent individual lines.
Product does not maintain reverse collections of CartItem or OrderItem.
Automatic timestamps are used according to each entity's lifecycle.
Important state changes should use intention-revealing domain methods.
Physical deletion is allowed only when historical or active references do not require the record to be preserved.

### Fetch strategy

The project currently keeps the default JPA fetch strategies:

@ManyToOne: eager by default;
@OneToMany: lazy by default;
@OneToOne: eager by default.

Do not change fetch behavior without a concrete query or performance problem and an explanation of the consequences.

### Cascade and orphan removal

Use CascadeType.ALL only when the parent owns the complete lifecycle of the child.

Current lifecycle ownership includes:

Cart -> CartItem
Order -> OrderItem
Product -> ProductImage

Use orphanRemoval = true only for children that cannot exist independently from their parent.

Do not cascade removal to entities with independent lifecycles.

Examples where automatic deletion must not propagate:

User -> Role
User -> Order
CartItem -> Product
OrderItem -> Product
### Product rules
SKU must be unique.
A Product references an existing Brand and Category.
Product requests receive brandId and categoryId.
A Product can have at most four images.
Product activation and deactivation use domain methods.
Activation and deactivation are exposed as explicit PATCH operations.
A Product referenced by CartItem or OrderItem must not be physically deleted.
When references exist, deletion behavior must preserve historical integrity.
ProductImage belongs to the Product lifecycle.

### Cart rules
Cart represents mutable shopping state.
CartItem stores Product and quantity.
CartItem does not store a product price snapshot.
Cart calculations use the current Product price.

### Order rules
Order represents completed historical data.
Order and OrderItem must preserve purchase history.
Historical records must not be silently changed or deleted.
OrderItem is conceptually different from CartItem even if some fields appear similar.

### Role rules
Roles are system-defined.
Mandatory roles are initialized by the application.
The current default roles are ADMIN, CUSTOMER, and SELLER.
Normal clients must not create, update, or delete system roles through unrestricted CRUD endpoints.

Do not invent new business rules.

When a requirement is missing or ambiguous, ask for clarification before implementing behavior that affects persistence or business history.

## 8. Monetary Values

Use BigDecimal for prices, totals, discounts, and all other monetary values.

Never use:

float;
double;
binary floating-point arithmetic for money.

Create monetary constants and test values using string constructors:

new BigDecimal("299.90")

Do not use:

new BigDecimal(299.90)

The second form can preserve binary floating-point imprecision.

Rounding mode and scale must be explicit when a calculation can produce additional decimal places.

Do not invent a rounding rule when the business requirement has not defined one.

## 9. Validation Rules

Separate syntactic validation from business validation.

### Request validation

Request DTOs handle input shape and basic constraints using Jakarta Bean Validation.

Examples:

@NotBlank
@NotNull
@Email
@Positive
@PositiveOrZero
@Size
@DecimalMin
### Business validation

Services and domain methods handle business decisions.

Examples:

checking whether a SKU already exists;
checking whether Brand and Category exist;
checking whether a Product has more than four images;
checking whether a Product can be deleted;
checking whether an order can change status;
checking whether requested stock is available.

Do not rely exclusively on controller validation for a business invariant.

## 10. HTTP and REST Conventions

Use resource-oriented endpoints and established HTTP semantics.

### Expected conventions

Operation	Method	Successful status
Create resource	POST	201 Created
Find all	GET	200 OK
Find by ID	GET	200 OK
Replace/update resource	PUT	200 OK
Partial state transition	PATCH	204 No Content or documented response
Delete resource	DELETE	204 No Content

### Expected errors include

Situation	Status
Request validation failure	400 Bad Request
Resource not found	404 Not Found
Business conflict or duplicate resource	409 Conflict
Unexpected internal failure	500 Internal Server Error without internal details

Keep endpoint behavior consistent with existing modules unless an intentional correction is being made.

## 11. Testing Strategy

All behavior changes must include or update automated tests.

### Service tests

Service tests should:

use JUnit 5;
use Mockito;
run without loading the Spring context when possible;
mock repositories and mappers;
verify successful behavior;
verify important failure scenarios;
verify that persistence is not called after a rejected operation.

Preferred setup:

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
}
### Controller tests

Controller tests should:

use Spring MVC testing and MockMvc;
mock the service;
test request validation;
test serialization and HTTP status;
verify that the service receives the expected request;
cover relevant exception responses.
### Test naming

Use behavior-oriented names:

method_shouldExpectedResult_whenCondition

Examples:

create_shouldCreateProduct_whenRequestIsValid()
create_shouldThrowException_whenSkuAlreadyExists()
findById_shouldReturnProduct_whenProductExists()
delete_shouldDeactivateProduct_whenHistoricalReferencesExist()
### Test principles
Keep tests deterministic.
Do not depend on execution order.
Do not use Thread.sleep.
Do not weaken or delete a valid test just to make the build pass.
Fix the implementation or discuss whether the expected behavior changed.
Avoid testing Lombok-generated getters and setters.
Test public behavior rather than private implementation details.
Use BigDecimal in monetary assertions.
Use meaningful test data.

A task is not complete when the implementation changes behavior but the corresponding tests are missing.

TDD may be used when requested or defined in an approved plan, but the repository does not require strict test-first development for every change.

## 12. Commands

Run commands from the repository root.

### Start PostgreSQL
docker compose up -d

### Stop PostgreSQL
docker compose down

### Run the application on Windows
mvnw.cmd spring-boot:run

### Run the application on Linux or macOS
./mvnw spring-boot:run

If the Maven Wrapper does not have execution permission:

bash ./mvnw spring-boot:run

### Run all tests on Windows
mvnw.cmd test

### Run all tests on Linux or macOS
./mvnw test

Alternative when execution permission is unavailable:

bash ./mvnw test

### Run one test class

Windows:

mvnw.cmd -Dtest=ProductServiceTest test

Linux or macOS:

./mvnw -Dtest=ProductServiceTest test

### Build the application

Windows:

mvnw.cmd clean verify

Linux or macOS:

./mvnw clean verify

Before declaring a code task complete, run the narrowest relevant tests first and then run the complete test suite when the environment allows it.

Never claim tests passed if they were not executed successfully.

When tests cannot run because of environment, network, database, or dependency issues, report the exact limitation separately from code failures.

## 13. Development Workflow

### Before changing code:

Read this AGENTS.md.
Inspect the requested module.
Read its entity, DTOs, mapper, repository, service, controller, and tests.
Search for a similar implementation already present.
Review the relevant business decisions in docs/06-decisions.md.
Identify the files that need to change.
Confirm ambiguous business behavior before implementing it.

### During implementation:

Make one coherent change at a time.
Keep controllers thin.
Keep business rules in services or domain methods.
Reuse existing exception and mapper patterns where appropriate.
Add or update tests for changed behavior.
Avoid unrelated refactoring.
Keep the project compiling after each meaningful step.

### After implementation:

Run the relevant test class.
Run the complete test suite when possible.
Review the diff.
Check for unrelated changes.
Check whether documentation became outdated.
Report changed files, test results, assumptions, and remaining work.
## 14. Documentation

Important project documentation lives in:

docs/01-project-overview.md
docs/02-architecture.md
docs/03-domain-model.md
docs/04-business-rules.md
docs/05-roadmap.md
docs/06-decisions.md
docs/PROJECT_CONTEXT.md

### Update documentation when a change affects:

architecture;
domain relationships;
business rules;
endpoint behavior;
project status;
implementation roadmap;
important technical decisions;
development setup.

Do not update every documentation file for every small implementation.

Update only the files whose statements became incorrect or incomplete.

docs/PROJECT_CONTEXT.md is a project snapshot, not a permanent source of truth.

When it conflicts with the current code, inspect the code and Git history before relying on it.

Do not mark planned functionality as implemented without confirming it in source code and tests.

## 15. Security and Sensitive Data

Never commit credentials, tokens, private keys, or production connection strings.
Do not place real user passwords in source code or tests.
Do not expose password hashes in response DTOs.
Do not log passwords, tokens, or authentication credentials.
Do not expose stack traces, database details, or internal class names through API responses.
Treat all client input as untrusted.
Use repository methods and parameterized persistence APIs instead of building SQL through string concatenation.
Do not add a dependency without confirming why it is necessary.
Authentication and authorization rules must not be invented before the security model is defined.

If a committed secret is found, stop the current task and report it.

Do not silently move, rotate, or delete credentials without authorization.

## 16. Scope Control

Only change files required by the current task.

Do not perform unrelated:

refactoring;
renaming;
formatting of entire files;
package restructuring;
dependency updates;
framework upgrades;
database redesign;
documentation rewrites.

If an additional change is necessary for the requested behavior, explain why before expanding the scope.

Do not introduce a generic abstraction for code that exists only once unless there is a clear near-term need.

Prefer a small amount of explicit duplication over a premature abstraction that makes the project harder to understand.

## 17. Prohibited Actions

Agents must not:

bypass failing tests;
remove validation to make a request succeed;
weaken assertions without a confirmed behavior change;
return entities directly from controllers;
access repositories from controllers;
use double or float for money;
add CascadeType.ALL without lifecycle ownership;
change fetch strategies without explaining the query impact;
expose unrestricted setters for important state transitions;
invent business requirements;
claim functionality exists without checking the source;
claim tests passed without executing them;
run destructive database commands without explicit authorization;
commit or push changes unless the user explicitly requests it;
deploy the application unless explicitly requested;
modify .github Harness files during an unrelated implementation task unless explicitly requested.
## 18. Definition of Done

A code task is complete only when:

the requested behavior is implemented;
the implementation respects the current architecture;
relevant business rules are preserved;
request and response contracts are appropriate;
expected errors are handled;
relevant automated tests exist;
relevant tests pass, or execution limitations are clearly reported;
unrelated files were not changed;
documentation affected by the change is updated;
assumptions and remaining limitations are reported.

### A planning task is complete only when:

affected modules are identified;
expected files are listed;
business rules are explicit;
dependencies between steps are clear;
each step has a validation method;
risks and unresolved questions are reported.

### A review task is complete only when:

findings are supported by concrete code evidence;
severity is explained;
file and location are identified;
suggested corrections are actionable;
style preferences are not presented as functional defects.
## 19. Agent Response Format

After implementing a change, report:

summary of the implemented behavior;
files created or modified;
tests executed and their results;
assumptions made;
work intentionally left outside the scope.

When reviewing code, classify findings as:

Blocker — causes incorrect behavior, security risk, data loss, broken build, or violated business rule;
Important — should be corrected before the feature is considered complete;
Recommendation — improvement that does not block the current behavior.

Do not invent findings to make a review appear more complete.

If no issue is found, state that no issue was identified in the reviewed scope.