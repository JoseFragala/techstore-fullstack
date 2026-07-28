package com.fragala.techstore.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity that represents the shipping address snapshot associated with an order.
 *
 * <p>This class exists because an order should usually keep the shipping address that was valid
 * at checkout time, even if the user later edits their saved addresses. Storing it separately
 * preserves historical accuracy.
 *
 * <p>Architecturally, this entity belongs to the persistence/domain layer and maps the
 * {@code order_shipping_addresses} table as a child of {@link Order}.
 *
 * <p>It is used when an order is created and when shipping details need to be displayed later.
 */
// `@Entity` marks this class as a persistent JPA entity.
@Entity
@Getter
@NoArgsConstructor
// `@Table` maps the class to the `order_shipping_addresses` table.
@Table(name = "order_shipping_addresses")
public class OrderShippingAddress {

    // Primary key that uniquely identifies the stored shipping-address snapshot.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // `@OneToOne` models that one order has one shipping address snapshot in this design.
    @OneToOne
    // This entity owns the relationship because the `order_id` foreign key is stored here.
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Setter
    @Column(nullable = false)
    private String street;

    @Setter
    @Column(nullable = false)
    private String number;

    @Setter
    @Column
    private String complement;

    @Setter
    @Column(nullable = false)
    private String neighborhood;

    @Setter
    @Column(nullable = false)
    private String city;

    @Setter
    @Column(nullable = false)
    private String state;

    @Setter
    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Setter
    @Column(nullable = false)
    private String country;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
