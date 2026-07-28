package com.fragala.techstore.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JPA entity that represents a single product entry inside a shopping cart.
 *
 * <p>This class exists because a cart-to-product relationship needs additional data, especially
 * quantity. Instead of a simple many-to-many mapping, a dedicated entity allows the relationship
 * itself to carry business data.
 *
 * <p>Architecturally, this entity belongs to the persistence/domain layer and maps the
 * {@code cart_items} table.
 *
 * <p>It is used whenever a user adds, updates, or removes a product from a cart.
 */
// `@Entity` makes this class persistent through JPA.
@Entity
@Getter
@NoArgsConstructor
// `@Table` maps the entity to the `cart_items` table.
@Table(name = "cart_items")
public class CartItem {

    // Primary key for the cart item row.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many cart items can belong to the same cart.
    @ManyToOne
    // The foreign key is stored in this table, so this entity owns the relationship to `Cart`.
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Many cart items may reference the same product because different carts can contain it.
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Quantity is stored here because it is specific to the relationship between cart and product.
    @Column(nullable = false)
    private Integer quantity;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    /**
     * Updates the quantity of this cart item.
     *
     * <p>Business responsibility: encapsulate quantity changes inside the entity instead of
     * letting callers mutate the field directly.
     *
     * @param quantity the new quantity that should be associated with this cart item
     */
    public void changeQuantity(Integer quantity){
        // Centralizing this update behind a method makes future validation or business rules
        // easier to introduce without changing all callers.
        this.quantity = quantity;
}

}
