# Roadmap

## Project Status

This document tracks the implementation progress of TechStore based on the current repository state and the current GitHub Project structure.

It is intended to remain a high-level development guide. It preserves completed work, highlights what is partially implemented, and organizes the remaining backend work in a practical order that respects the current architecture.

Last updated from local source and tests: October 18, 2026. GitHub Project status was not synchronized during this update.

---

# Phase 1 - Domain Model

## Objective

Model the core e-commerce business domain before expanding the persistence, business, API, validation, security, testing, and infrastructure layers.

## Current Status

`Completed`

The domain model is the strongest completed part of the project. Core entities already exist in source code and remain the completed foundation for the rest of the roadmap.

## Milestones

### Milestone 1.1 - Feature-Based Domain Model

- Current progress: `Completed`
- Dependencies: None
- Expected deliverables:
  - [x] `Role`
  - [x] `User`
  - [x] `Address`
  - [x] `Brand`
  - [x] `Category`
  - [x] `Product`
  - [x] `ProductImage`
  - [x] `Cart`
  - [x] `CartItem`
  - [x] `Order`
  - [x] `OrderItem`
  - [x] `OrderShippingAddress`

## Remaining Work

- Verify entity-to-schema alignment as later phases mature
- Continue using the existing domain model as the basis for repositories, services, and APIs

## Learning Objectives

- Entity modeling
- JPA relationships
- Aggregate boundaries
- Business-first design
- Separation between mutable cart state and immutable order history

## Definition of Done

- Core domain entities are modeled
- Main relationships are defined
- Key business concepts are represented in code
- Domain documentation exists and matches the source at a high level

---

# Phase 2 - Persistence

## Objective

Establish a reliable persistence layer that reflects the current domain model and supports the service roadmap defined in the GitHub Project.

## Current Status

`Completed`

All repositories listed in this milestone exist, including AddressRepository. This confirms repository availability, not complete persistence test coverage.

## Milestones

### Milestone 2.1 - Feature-Based Persistence

- Current progress: `Completed`
- Dependencies:
  - Phase 1 - Domain Model
- Expected deliverables:
  - [x] `Category Repository`
  - [x] `Brand Repository`
  - [x] `Product Repository`
  - [x] `User Repository`
  - [x] `Address Repository`
  - [x] `Cart Repository`
  - [x] `Order Repository`

## Remaining Work

- Add persistence queries only when required by new use cases
- Keep repository naming and scope aligned with the GitHub Project items
- Review entity-to-schema consistency as persistence expands

## Learning Objectives

- Repository pattern
- relational schema design
- persistence consistency
- Spring Data JPA structure
- incremental persistence coverage

## Definition of Done

- Each planned persistence item has a corresponding repository
- Repository coverage supports the service roadmap
- Persistence responsibilities remain consistent across the project

---

# Phase 3 - Business Layer

## Objective

Move from pure domain modeling into use-case implementation through service classes organized around the feature roadmap.

## Current Status

`Partially completed`

Category, brand, product, user, address, and read-only role services exist. Cart and order use cases remain unimplemented. Address default replacement has transactional rollback coverage.

## Milestones

### Milestone 3.1 - Feature-Based Business Layer

- Current progress: `Partially completed`
- Dependencies:
  - Phase 2 - Persistence
- Expected deliverables:
  - [x] `Category Service`
  - [x] `Brand Service`
  - [x] `Product Service`
  - [x] `User Service`
  - [x] `Address Service`
  - [ ] `Cart Service`
  - [ ] `Order Service`

## Remaining Work

- Define cart business rules before implementing CartService, then plan order workflows
- Preserve historical integrity when implementing order workflows
- Keep controllers thin by centralizing use cases in services

## Learning Objectives

- service-layer responsibilities
- constructor injection
- business workflows
- use-case organization
- separation between API and business logic

## Definition of Done

- Each planned business item has a corresponding implemented service
- Core business use cases are handled outside controllers
- Service responsibilities are consistent across modules

---

# Phase 4 - API

## Objective

Expose the business layer through a clear REST API organized around the same feature roadmap used by the GitHub Project.

## Current Status

`Partially completed`

Category, brand, product, user, and address controllers expose DTO-based APIs. Roles have a read-only API. Authentication and authorization are still absent; API availability does not imply production readiness.

## Milestones

### Milestone 4.1 - Feature-Based API

- Current progress: `Partially completed`
- Dependencies:
  - Phase 3 - Business Layer
- Expected deliverables:
  - [x] `Category API`
  - [x] `Brand API`
  - [x] `Product API`
  - [x] `User API`
  - [x] `Address API`
  - [ ] `Cart API`
  - [ ] `Order API`

## Remaining Work

- Add cart and order endpoints after their business rules and services are defined
- Extend API coverage feature by feature across the roadmap
- Keep DTO boundaries and response behavior consistent

## Learning Objectives

- REST controller design
- HTTP methods and status codes
- DTO patterns
- API contract design
- predictable response structure

## Definition of Done

- Each planned API item has a corresponding endpoint layer
- Main use cases are exposed through REST endpoints
- API boundaries stay consistent across modules

---

# Phase 5 - Validation

## Objective

Add a dedicated validation and error-handling phase between API and security so request and business failures are handled consistently.

## Current Status

`Partially completed`

Request DTOs use Bean Validation. GlobalExceptionHandler translates validation errors, missing resources, and category, brand, and user duplicates into ApiError responses. Some other failures still need explicit handling; coverage is partial.

## Milestones

### Milestone 5.1 - Feature-Based Validation

- Current progress: `Partially completed`
- Dependencies:
  - Phase 4 - API
- Expected deliverables:
  - [x] `Bean Validation`
  - [x] `Global Exception Handler`
  - [x] `Business Exceptions`
  - [ ] `Error Response Standardization` (ApiError exists; complete remaining failure coverage)

## Remaining Work

- Extend existing request validation to new APIs
- Complete explicit error translation for remaining business and unexpected failures
- Standardize error payloads before introducing security flows

## Learning Objectives

- Bean Validation
- exception handling
- business error design
- API error contracts
- consistent failure behavior

## Definition of Done

- Validation rules are enforced consistently
- Exceptions are translated predictably
- Error responses follow a stable standard across the API

---

# Phase 6 - Security

## Objective

Protect the API with authentication and authorization after the validation layer is in place.

## Current Status

`Partially completed`

Password hashing is implemented with spring-security-crypto and a PBKDF2 PasswordEncoder. No web security configuration, authentication endpoint, JWT flow, or authorization enforcement exists. The approved policy is public registration as CUSTOMER and role assignment only by authenticated ADMIN; current requests still accept roleId without enforcing that policy. See 08-security.md.

## Milestones

### Milestone 6.1 - Feature-Based Security

- Current progress: `Partially completed`
- Dependencies:
  - Phase 5 - Validation
- Expected deliverables:
  - [ ] `Spring Security Configuration`
  - [ ] `Authentication API`
  - [ ] `JWT Authentication`
  - [ ] `Role Authorization`
  - [x] `Password Hashing`

## Remaining Work

- Implement authentication and enforce the approved role assignment policy before use with real data
- Align authentication design with the existing `User` and `Role` model
- Enforce role assignment on authenticated endpoints; plan legacy password resets or migration explicitly

## Learning Objectives

- Spring Security basics
- authentication and authorization
- password storage
- stateless API security with JWT
- role-based access control

## Definition of Done

- Authentication is implemented
- Protected endpoints enforce authorization rules
- Security is integrated without breaking core API behavior

---

# Phase 7 - Testing

## Objective

Build confidence in the backend through automated tests aligned with the repository, service, controller, and integration layers of the roadmap.

## Current Status

`Partially completed`

Service and MockMvc controller tests cover category, brand, product, role, address, and user modules. Address integration tests use a dedicated PostgreSQL database to verify rollback and successful default replacement. User integration tests verify persisted password hashes on creation and password changes. Password encoder tests exercise real hashing and password matching. Coverage is incremental, not exhaustive.

## Milestones

### Milestone 7.1 - Feature-Based Testing

- Current progress: `Partially completed`
- Dependencies:
  - Implemented use cases in Phases 2 through 5 (tests accompany behavior changes)
- Expected deliverables:
  - [ ] `Repository Tests`
  - [x] `Service Tests`
  - [x] `Controller Tests`
  - [x] `Integration Tests`
  - [ ] `Testcontainers`

## Remaining Work

- Expand behavioral coverage as new use cases are implemented
- Add tests incrementally alongside repositories, services, and APIs
- Reduce manual environment dependency during test execution

## Learning Objectives

- unit testing
- integration testing
- test isolation
- database-backed testing
- confidence-driven refactoring

## Definition of Done

- Core modules have automated tests
- Test execution is reliable
- Critical business behavior is covered by repeatable test suites

---

# Phase 8 - Infrastructure

## Objective

Prepare the backend for consistent local execution and future deployment after the main application layers are in place.

## Current Status

`Partially completed`

The project already includes database container support through Docker Compose, but the rest of the infrastructure roadmap is still open.

## Milestones

### Milestone 8.1 - Feature-Based Infrastructure

- Current progress: `Partially completed`
- Dependencies:
  - Phase 7 - Testing
- Expected deliverables:
  - [ ] `Dockerfile`
  - [x] `Docker Compose`
  - [ ] `CI/CD`
  - [ ] `Azure Deployment`

## Remaining Work

- Add application containerization beyond the existing database setup
- Introduce automated delivery workflows after testing is in place
- Prepare deployment structure for the planned Azure target

## Learning Objectives

- Docker-based local infrastructure
- application packaging
- deployment basics
- CI/CD fundamentals
- environment portability

## Definition of Done

- The backend can be built and run consistently
- Infrastructure supports testing and deployment workflows
- Deployment setup is no longer purely local and manual

---

# Future Features

These items remain part of the long-term product vision and stay outside the current backend implementation roadmap.

- [ ] Wishlist
- [ ] Product Reviews
- [ ] Discount Coupons
- [ ] Payment Integration
- [ ] Shipment Tracking
- [ ] Inventory Reservation
- [ ] Email Notifications
- [ ] Remarketing Campaigns

---

# Current Focus

- Current phase: complete user testing and password storage, then define cart use cases
- Current milestone: user module hardening and security planning
- Next implementation task: plan authentication and enforcement of the approved role policy; define cart rules before cart implementation
- Next milestone: cart service/API and authentication/authorization in explicitly scoped increments
- Overall progress is tracked by verified deliverables; no percentage estimate is maintained
