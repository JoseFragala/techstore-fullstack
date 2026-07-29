# Roadmap

## Project Status

This document tracks the implementation progress of TechStore based on the current repository state and the current GitHub Project structure.

It is intended to remain a high-level development guide. It preserves completed work, highlights what is partially implemented, and organizes the remaining backend work in a practical order that respects the current architecture.

Last updated from repository analysis and GitHub Project sync: July 29, 2026.

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

`Partially completed`

The repository already includes most of the planned Spring Data repositories, but persistence coverage is not fully complete yet.

## Milestones

### Milestone 2.1 - Feature-Based Persistence

- Current progress: `Partially completed`
- Dependencies:
  - Phase 1 - Domain Model
- Expected deliverables:
  - [x] `Category Repository`
  - [x] `Brand Repository`
  - [x] `Product Repository`
  - [x] `User Repository`
  - [ ] `Address Repository`
  - [x] `Cart Repository`
  - [x] `Order Repository`

## Remaining Work

- Add the missing repository coverage required by the roadmap
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

The business layer has started with category creation and a placeholder user service, but the feature-based service roadmap is still mostly open.

## Milestones

### Milestone 3.1 - Feature-Based Business Layer

- Current progress: `Partially completed`
- Dependencies:
  - Phase 2 - Persistence
- Expected deliverables:
  - [x] `Category Service`
  - [ ] `Brand Service`
  - [ ] `Product Service`
  - [ ] `User Service`
  - [ ] `Address Service`
  - [ ] `Cart Service`
  - [ ] `Order Service`

## Remaining Work

- Expand from the existing category slice into the rest of the service roadmap
- Replace placeholders with implemented business workflows
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

The category flow already has DTO groundwork, but none of the planned APIs are fully implemented yet.

## Milestones

### Milestone 4.1 - Feature-Based API

- Current progress: `Partially completed`
- Dependencies:
  - Phase 3 - Business Layer
- Expected deliverables:
  - [ ] `Category API`
  - [ ] `Brand API`
  - [ ] `Product API`
  - [ ] `User API`
  - [ ] `Address API`
  - [ ] `Cart API`
  - [ ] `Order API`

## Remaining Work

- Implement the first controller slice around the existing category flow
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

`Not started`

No Bean Validation annotations, global exception handling, business exception layer, or standardized error response contract are implemented yet.

## Milestones

### Milestone 5.1 - Feature-Based Validation

- Current progress: `Not started`
- Dependencies:
  - Phase 4 - API
- Expected deliverables:
  - [ ] `Bean Validation`
  - [ ] `Global Exception Handler`
  - [ ] `Business Exceptions`
  - [ ] `Error Response Standardization`

## Remaining Work

- Add request validation once the first APIs are in place
- Define a consistent exception strategy for business and API failures
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

`Not started`

Security remains planned work. No Spring Security configuration, authentication endpoint, JWT flow, or authorization layer exists yet.

## Milestones

### Milestone 6.1 - Feature-Based Security

- Current progress: `Not started`
- Dependencies:
  - Phase 5 - Validation
- Expected deliverables:
  - [ ] `Spring Security Configuration`
  - [ ] `Authentication API`
  - [ ] `JWT Authentication`
  - [ ] `Role Authorization`
  - [ ] `Password Encryption`

## Remaining Work

- Introduce security after validation and error handling are consistent
- Align authentication design with the existing `User` and `Role` model
- Protect endpoints without breaking the planned API contracts

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

`Not started`

The repository contains only a Spring Boot context test, so the feature-based testing roadmap has not started yet.

## Milestones

### Milestone 7.1 - Feature-Based Testing

- Current progress: `Not started`
- Dependencies:
  - Phase 6 - Security
- Expected deliverables:
  - [ ] `Repository Tests`
  - [ ] `Service Tests`
  - [ ] `Controller Tests`
  - [ ] `Integration Tests`
  - [ ] `Testcontainers`

## Remaining Work

- Move beyond context startup checks into behavioral testing
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

- Current phase: `Phase 2 - Persistence`
- Current milestone: `Milestone 2.1 - Feature-Based Persistence`
- Next implementation task: add the missing `Address Repository` to complete repository coverage before expanding the broader service roadmap
- Next milestone: `Phase 3, Milestone 3.1 - Feature-Based Business Layer`
- Overall project progress estimate: `25% to 30%`
