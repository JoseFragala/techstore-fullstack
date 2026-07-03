# Architecture

## Overview

TechStore follows a layered architecture, where each layer has a single responsability.

The objective is to keep code organized, maintainable, and easy to extend as the project grows.

---

## Layers

```
Controller
    │
    ▼
Service
    │
    ▼
Repository
    │
    ▼
Database
```

### Controller

Responsible for handling HTTP requests and returning HTTP responses.

Responsibilities:

- Receive client request.
- Validate input.
- Call the appropriate service
- Return the response.

Business rules should not be implemented here.

---

### Service

Contains the application's business logic.

Responsabilities:

- Coordinate business workflows.
- Validate business rules.
- Interact with repositories.
- Coordinate multiple entities when necessary.

---

### Repository

Responsible for database access.

Responsibilities:

- Query data.
- Save entities.
- Update entities.
- Delete entities.

Repositories should not contain buiness logic.

---

### Entity

Represents the application's domain model.

Responsabilities:

- Represent business concepts.
- Define relationships between entities.
- Encapsulate state and business behavior

Entities should not know about controllers, repositories, or HTTP.

---

## Design Principles

The project follows these principles:

- Separation of Concerns (SoC)
- Single Responsability Principle (SRP)
- Domain-first design
- Business rules before implementation
- Encapsulation
- Object-oriented modeling


## Development Workflow

Every new feature follows the same process:

1. Understand the business requirement.
2. Model the domain.
3. Define entity relationships.
4. Implement the persistence model.
5. Review architectural decisions.
6. Implement repositories, services, and controllers.
7. Refactor when necessary.





