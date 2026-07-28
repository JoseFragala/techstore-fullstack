package com.fragala.techstore.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
 * JPA entity that represents a purchased line item inside an order.
 *
 * <p>This class exists because an order needs to store not only which products were purchased,
 * but also the quantity and the unit price captured at purchase time. That makes it a classic
 * relationship entity with additional business data.
 *
 * <p>Architecturally, this entity belongs to the domain/persistence layer and maps the
 * {@code order_items} table as a child of {@link Order}.
 *
 * <p>It is used when an order is created and later when order contents need to be reviewed.
 */
// `@Entity` allows JPA to persist this class as part of the relational model.
@Entity
@Getter
@NoArgsConstructor
// `@Table` maps this entity to the `order_items` table.
@Table(name = "order_items")
public class OrderItem {

    // Primary key that uniquely identifies the order item row.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many order items belong to one order because each order can contain multiple products.
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Many order items may point to the same product across different orders.
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Quantity records how many units of the product were purchased in this order line.
    @Column(nullable = false)
    private Integer quantity;

    // The unit price is stored here as a snapshot so historical orders remain accurate even if the
    // product price changes later.
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
