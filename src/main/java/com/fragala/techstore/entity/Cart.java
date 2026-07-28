package com.fragala.techstore.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JPA entity that represents a shopping cart owned by a user.
 *
 * <p>This class exists to hold the temporary list of items a user intends to buy before placing
 * an order. In an e-commerce architecture, the cart is an important aggregate because it groups
 * the current purchasing intent and its items.
 *
 * <p>Architecturally, this entity belongs to the domain/persistence layer and maps the
 * {@code carts} table together with its child cart items.
 *
 * <p>It is used while a user is actively building or updating a purchase.
 */
// `@Entity` marks this class as a database-mapped JPA entity.
@Entity
@NoArgsConstructor
@Getter
// `@Table` maps the entity to the `carts` table.
@Table(name = "carts")  
public class Cart {

    // Primary key that uniquely identifies the cart row.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // `@OneToOne` means one cart belongs to exactly one user in this model.
    // The unique foreign key enforces that a user cannot have multiple carts in this table.
    @OneToOne
    // The cart owns this relationship because the `user_id` foreign key is stored in `carts`.
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One cart can contain many cart items.
    // `mappedBy = "cart"` means `CartItem` owns the relationship because it stores the foreign key.
    // `cascade = CascadeType.ALL` propagates persistence operations from the cart to its items,
    // which is useful because cart items are part of the cart aggregate lifecycle.
    // `orphanRemoval = true` removes child rows that are no longer referenced in this collection,
    // preventing stale cart items from remaining in the database.
    @OneToMany(mappedBy = "cart", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;


    /**
     * Checks whether the cart currently has any items.
     *
     * <p>Business responsibility: provide a domain-friendly way to ask whether the cart is empty
     * without exposing collection-checking logic to callers.
     *
     * @return {@code true} when the cart has no items, otherwise {@code false}
     */
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    /**
     * Removes all items from the cart.
     *
     * <p>Business responsibility: clear the cart aggregate in one operation. Because orphan
     * removal is enabled, clearing the collection also signals that the child rows should be
     * removed from the database.
     */
    public void clear() {
        // Clearing the collection updates the in-memory aggregate first. JPA will later translate
        // that state change into delete operations for the orphaned cart items.
        cartItems.clear();
    }

}
