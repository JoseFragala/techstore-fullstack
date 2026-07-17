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
- A user can have at most one default address.
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

## Cart Items

- Every cart item references exactly one product.
- The same product should not appear twice in the same cart.
- Increasing the quantity should update the existing cart item istead of creating another one.
- Item quantity must be greater than zero.
- Cart pricing is dynamic and uses the current Product.price.
- Adding a product to the cart does not freeze its price.


---

# Orders

## Checkout

- Checkout converts the current cart into an order.
- Every order belongs to exactly one user.
- Every order must contain at least one order item.
- Every order must have one shipping-address snapshot copied from the selected user address at checkout.
- The shipping snapshot must remain independent from future edits to the user's address book.
- Order status is stored as one of: PENDING, PAID, SHIPPED, DELIVERED, CANCELLED.

---

## Order Items

- Every order item references exactly one product. 
- The purchase price must be stored at the time of checkout.
- The purchesed quantity must be stored.
- Order items become immutable after checkout.

---

## Auditing

- User, Address, Product, Brand, Category, Cart, and Order use creation and update timestamps.
- Role and ProductImage use creation timestamps only at this stage.
- CartItem uses creation and update timestamps because cart lines remain mutable while the cart is active.
- OrderItem and OrderShippingAddress preserve historical state and use creation timestamps without update timestamps.

---

# Genearl Rules

- Creation timestamps are generated automatically.
- Update timestaps are maintained automatically only for entities that remain mutable after creation.
- Database identifiers are generated automatically.



