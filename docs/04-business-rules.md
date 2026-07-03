# Business Rules

## Overview

This document defines the business rules that govern the TechStore application.

These rules describe how the system must behave idependently of the implementation.

---

# Users

## User Registration

- Every user must have a unique email address.
- Passwords must always be stored encrypted.
- Every user must have exactly one role.
- A newly created user is active by default.

---

## Addresses

- A user can have multiple addresses.
- Every address belongs to exactly one user.
- A user can have only one default addresses.
- The default address is used during checkout unless another address is selected.

---

# Product Catalog

## Products

- Every product belongs to exactly one brand.
- Every product belongs to exactly one category.
- A product can have multiple images.
- Every product must have a unique SKU.
- Product prices must use decimal precision.
- Products can be activated or deactivated.
- Inactive products cannot be purchased. 


---

## Iventory

- Stock cannot be negative.
- Stock is reduced only after a sucessful checkout.
- Stock is increased thtough inventory operations.

---

# Shopping Cart

## Cart

- Every user has one active shopping cart.
- Every shopping cart belongs to exaclty one user.
- A cart can contain mulple cart items.

---

## Cart Items.

- Every cart item references exactly one product.
- The same product should not appear twice in the same cart.
- Increasing the quantity should update the existing cart item istead of creating another one.
- Item quantity must be greater than zero. 


---

# Orders

## Checkout

- Checkout converts the current cart into an order.
- Every order belongs to exactly one user.
- Every order must contain at least one order item.
- Every order must have one shipping address.

---

## Order Items

- Every order item references exactly one product. 
- The purchase price must be stored at the time of checkout.
- The purchesed quantity must be stored.
- Order items become immutable after checkout.

---

# Genearl Rules

- Creation timestamps are generated automatically.
- Update timestaps are maintained automatically.
- Database identifiers are generated automatically.



