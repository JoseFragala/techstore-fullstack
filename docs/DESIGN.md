# TechStore Design

## Overview

TechStore is a learning-focused e-commerce project built around a layered Spring Boot application. The design keeps responsibilities simple and explicit: HTTP concerns live in controllers, business rules live in services, persistence lives in repositories, and the database stores the domain model.

The project aims to model a realistic commerce workflow without introducing unnecessary architectural complexity. The design favors clarity, testability, and gradual learning over enterprise abstractions.

## Architectural Style

The application follows a standard layered architecture:

HTTP request
  -> Controller
  -> Service
  -> Repository
  -> PostgreSQL

A mapper layer converts entities and DTOs when needed, but it does not contain business decisions or persistence logic.

## Main Domain Areas

### Catalog

- Brand
- Category
- Product
- ProductImage

The product catalog is the core of the store. Each product belongs to an existing brand and category, and product images are part of the product lifecycle.

### User and Access

- User
- Role
- Address

Roles are system-defined and initialized by the application. User and address data are managed through the domain model without exposing persistence details to the API.

### Cart and Order

- Cart
- CartItem
- Order
- OrderItem

Cart represents mutable shopping state. Order represents completed historical data. This separation preserves business history and allows the system to model purchase records without silently changing past orders.

## Design Principles

- Keep the service layer responsible for business rules.
- Keep controllers thin and focused on HTTP concerns.
- Use DTOs for public API contracts.
- Use domain methods for important state transitions.
- Preserve historical integrity when records are referenced by orders or carts.
- Prefer clear, direct implementations over abstraction for its own sake.
- Use BigDecimal for all monetary values.

## Persistence Rules

The application uses JPA and object references for entity relationships rather than primitive foreign keys. Product deletion follows business safety rules: physical deletion is avoided when historical or active references must remain intact.

The current model also follows a few important conventions:

- Product SKU must be unique.
- Product activation and deactivation use explicit domain methods.
- CartItem and OrderItem are distinct concepts even if they appear similar.
- ProductImage belongs to the lifecycle of Product.
- Cascade and orphan removal are used carefully and only where ownership is clear.

## API Conventions

The API is organized around resource-oriented endpoints and uses standard HTTP semantics:

- POST creates a resource and returns 201 Created.
- GET retrieves one or many resources with 200 OK.
- PUT updates a resource and returns 200 OK.
- PATCH is used for explicit state transitions such as activation or deactivation.
- DELETE removes a resource or deactivates it when historical references exist, returning 204 No Content.

Expected validation and error handling follow the project contract:

- invalid input -> 400 Bad Request
- missing resource -> 404 Not Found
- business conflict -> 409 Conflict
- unexpected internal failure -> 500 Internal Server Error without exposing internals

## Testing Strategy

Automated tests cover both service behavior and controller behavior:

- Service tests validate business logic in isolation.
- Controller tests validate request validation, HTTP responses, and serialization.
- Tests focus on public behavior rather than implementation details.

This keeps the project stable while preserving the intended business rules.

## Evolution

The current design is intentionally simple and grounded in the learning goals of the project. It is designed to support incremental growth into authentication, order workflows, API refinement, and frontend integration without changing the underlying domain structure unnecessarily.
