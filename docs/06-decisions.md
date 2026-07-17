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

Methods such as activate(), deactivate(), or chagePassword() express business intent more clearly than generic setters and help protect the integrity of domain model. 

