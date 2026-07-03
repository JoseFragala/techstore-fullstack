# Domain Model

## Overview

The domain model represents the core business concepts of TechStore.

Instead of focusing on database tables, the model describes how the business entities relate to each other and the role each one plays in the application.

---

# User Management

## Role

Represents the permissions assigned to a user.

Relationships:

- One Role can be assigned to many Users.

--- 

## User

Represents a customer registered in the e-commerce.

Relationships:

- One Role
- One Cart
- Many Addresses 
- Many Orders

---

## Address

Represents a shipping address associated with a user.

Relationships:

- One User

Business purpose:

- Store shipping information.
- Allow users to manage multiple delivery locations.

---

# Product Catalog

## Brand

Represents the manufacture of a product.

Reltionships:

- Many Products

---

## Category

Represents the category where prodcuts are organized.

Relationships:

- Many Products

---

## Product

Represents a product available for sale.

Relationships:

- One Brand
- One Category
- Many Product Images

Business purpose:

- Store product information.
- Manage stock.
- Define pricing.


---

## ProductImage

Represents an image associated with a product.

Relationships:

- One Product

Business purpose:

- Allow multiple images for each product.
- Support image ordering for product galleries.

---

# Shopping

## Cart

Represents the current shopping session of a user.

Relationships:

- One User
- Many Cart Items

Business purpose:

- Store products before checkout.

---

## CartItem

Represents a single line inside a shopping cart.

Relationships:

- One Cart
- One Product

Business purpose:

- Store the selected product.
- Store quantity
- Store the product price while the item remains in the cart.

---

# Orders

## Order

Represents a completed purchase. 

Relationships:

- One User
- One Adress
- Many Order Items

Business purpose:

- Preserve the purchase history.

---

## OrderItem

Represents one purchased product inside an order.

Relationships:

- One Order
- One Product

Business purpose:

- Preserve the purchased product.
- Preserve Quantity.
- Preserve the purchase price.








