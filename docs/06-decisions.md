# Architecture Decisions

## Overview

This document records important architectural decisions made during the development of TechStore.

The purpose is to explain why a decisions was made, allowing future contributors (and future versions of myself) to understand the reasioning behind the current design.

# 001 - Domain First Development

## Decision

The project is developed by modeling the domain before implementing repositories, services, or controllers.

## Rationale

Understanding the business domain first leads to better object-oriented design and cleaner application architecture.

Instead of building CRUD endpoints immediately, the project prioritizes modeling the business concepts and their relationships.

---

# 002 - Entities Use Object References Instead of Foreign Key IDs

## Decision

Entity relationships are represented using object references rather than primitive indentifier fields.

Example:

User - Role

Instead of 

User - roleId

## Rationale

This approach embraces object-oriented programming and allows JPA to manage relationships naturally.

---

# 003 - Cart and Order Are Separate Conceps

## Decision

Shopping carts and orders are represented by different entities.

## Rationale

A shopping cart represents a temporary state that changes frequently.

An order represents a completed purchase and becomes part of the application's history.

Keeping them separated simplifies the business model.

---

## 004 - CartItem and OrderItem Represent Individual Lines

## Decision

Product does not maintain collections of CartItem or OrderItem.

## Rationale

The application naturally navigates from Cart or Order to Product.

The reverse navigation is unnecessary and would increase complexity without provinding business value.

---

# 006 - Automatic Auditing

## Decision

Entities use automatic timestamps according to lifecycle needs instead of forcing the same auditing fields onto every entity.

## Rationale

Audit information should be generated automatically, but immutable historical records do not need the same update semantics as mutable entities.

---

# 007 - Rich Domain Model

## Decision 

Entities should gradually encapsulate business behavior instead of exposing unrestricted setters.

## Rationale

Methods such as activate(), deactivate(), chagePassword(), changePhone(), changeEmail(), etc. express business intent more clearly than generic setters and help protect the integrity of domain model. 

# 008 - Default Fetch Strategy

## Decision

The project keeps Hibernate's default fetch strategies.

ManyToOne - Eager
OneToMany - Lazy
OneToOne - Eager

## Rationale

Current project size does not justify overriding the defaults.

# 009 - Cascade Usage

## Decision 

CascadeType.All is only used when the parent entity owns the complete lifecycle of the child entity.

currently used on:

- Cart -> CartItem
- Order -> OrderItem
- Product -> ProductImage

## Rationale

Those child entities have no meaning wihtout their parent.
Deleting the parent should also delete its owned childre.

Not used on
- User -> Order
- CartItem -> Product
- OrderItem -> Product
- User -> Role


## Rationale

These entities have independent lifecycles and should never be deleted automatically.

# 010 - Orphan Removal

## Decision

orpanRemoval = true is used together with CascadeType.ALL for aggregate-owned child collections. 

## Rationale

Removing a child from the parent's collection should permanently remove it from the datavase. 



# 011 - Aggregate Ownership

## Decision

Some entities are treated ass aggregate roots.

# 012 - CartItem does not store product price

## Decision 

CartItem stores only:
- Prduct
- Quantity

it does not store the product price. 

## Rationale

The shopping cart should always reflect the current product price.


# 013 - No dedicated undo operation

## Decision

The system does not provide a dedicated undo operation.

If incorrect data is created, it should be corrected through the appropriate update operation.

## Rationale

A separate undo mechanism would add unnecessary complexity when the existing update operation is sufficient to correct the data.

# 014 - Deletion depends on historical data

## Decision

A record can be physically deleted only when it has no historical data that depends on it.

If historical data exists, the record must be deactivated instead of physically deleted.

## Rationale

Historical records must remain consistent and preserve the history of the e-commerce system.

# 015 - Product References Brand and Category by ID in Requests

## Decision

CreateProductRequest and UpdateProductRequest receive `brandId` and `categoryId` instead of complete Brand or Category objects.

## Rationale

The client only needs to identify which existing Brand and Category should be associated with the Product.

The Service is responsible for resolving these IDs into existing entities before creating or updating the Product.

This keeps the API request simple and avoids exposing persistence entities directly through request DTOs.


# 016 - Product Has a Maximum of Four Images

## Decision

A Product can have a maximum of four ProductImages.

## Rationale

The image limit is a business rule of the Product.

The Service validates this rule during Product creation before persisting the Product.

Keeping this validation in the Service also ensures that the rule is not dependent only on HTTP request validation.

# 017 - Active State Is Controlled Through Domain Methods

## Decision

The Product active state is not exposed through a public setter.

The Product provides `activate()` and `deactivate()` methods to change its active state.

## Rationale

`active` represents business state rather than simple data.

Methods such as `activate()` and `deactivate()` express business intent more clearly than generic setters and prevent unrestricted modification of the state.

This follows the Rich Domain Model principle already adopted by the project.

# 018 - Product Activation and Deactivation Use Explicit API Operations

## Decision

Product activation and deactivation are exposed through dedicated PATCH endpoints:

PATCH /products/{id}/activate

PATCH /products/{id}/deactivate

## Rationale

Activation and deactivation represent explicit state transitions rather than general Product updates.

Dedicated operations make the intent of the API request clear and prevent the client from directly manipulating the `active` field.

Successful operations return HTTP 204 No Content.

# 019 - Product Deletion Depends on Existing References

## Decision

A Product can be physically deleted only when it has no references from CartItem or OrderItem.

If either reference exists, the Product is deactivated instead of physically deleted.

## Rationale

CartItem and OrderItem reference Product through mandatory relationships.

OrderItem also represents historical purchase information, so deleting a referenced Product could compromise the integrity of existing data.

When references exist, deactivation preserves the Product record while preventing it from being treated as an active catalog item.

# 020 - ProductImage Lifecycle Is Owned by Product

## Decision

ProductImage is treated as a lifecycle-owned child of Product.

Product uses:

CascadeType.ALL

and:

orphanRemoval = true


## Rationale

A ProductImage has no independent business meaning outside its Product.

Therefore, Product controls the persistence lifecycle of its images.

Adding or removing images through the Product aggregate keeps the relationship synchronized and allows orphaned images to be removed automatically.

# 021 - Roles Are System-Defined

## Decision

User roles are predefined and controlled by the application.

The system currently provides the following roles:

- ADMIN;
- CUSTOMER;
- SELLER.

Roles are created automatically during application startup by the RoleDataInitializer.

Users can be associated with an existing role, but roles are not created dynamically trough the  public API.


## Rationale

Roles define authorization concepts used by the application and are part of the system's security model.

Allowing clients to create arbitrary roles through the API could introduce authorization concepts that are not 
recognized or properly handled by the application. 

Keeping roles System-defined ensures that the available authorization roles remain controlled by the application and
provides a stable foundation for future authentication and authorization. 

# 022 - Roles Are Not Deleted Through the Public API

## Decision

Roles cannot be physically deleted or modified through the public API.

the role api is read-only and only exposes operations for retrieving exisitng roles.


## Rationale
Roles are referenced by users and represent part of the authorization model.

Deleting or arbitrarily modifying a role could invalidate existing user associations and compromise the consistency of the authorization model.

Because roles are system-defined, their lifecycle is controlled by the application rather than by regular API consumers.


