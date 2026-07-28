package com.fragala.techstore.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JPA entity that represents a finalized purchase order.
 *
 * <p>This class exists to capture the result of a checkout process: who placed the order, which
 * items were purchased, where they should be shipped, the current workflow status, and the total
 * amount.
 *
 * <p>Architecturally, this is a central domain entity in the persistence layer. It maps the
 * {@code orders} table and acts as the parent aggregate for order items and shipping address.
 *
 * <p>It is used after a cart is converted into a formal order and throughout the order lifecycle.
 */
// `@Entity` tells JPA that this class should be persisted as a table-backed entity.
@Entity
@Getter
@NoArgsConstructor
// `@Table` binds the entity to the `orders` table.
@Table (name = "orders")
public class Order {

    // Primary key that uniquely identifies each order.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many orders can belong to the same user because one customer can buy multiple times.
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // One order contains many line items.
    // `mappedBy = "order"` means `OrderItem` owns the relationship through its foreign key.
    // `cascade = CascadeType.ALL` keeps child order items in sync with the parent order lifecycle.
    // `orphanRemoval = true` ensures removed items are deleted instead of remaining detached
    // in the database.
    @OneToMany(mappedBy = "order", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    // Each order has one shipping address snapshot in this model.
    // `mappedBy = "order"` means the foreign key is stored in `OrderShippingAddress`, so this
    // side is the inverse side of the relationship.
    // Cascade and orphan removal are useful because the shipping address is part of the order
    // aggregate and should usually live and die with the order.
    @OneToOne(mappedBy = "order", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private OrderShippingAddress shippingAddress;

    // `@Enumerated(EnumType.STRING)` stores the enum name as text in the database.
    // Using strings is usually easier to read and safer than ordinal numbers when enum order changes.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    // The order total is stored as BigDecimal to avoid floating-point rounding issues in money values.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
