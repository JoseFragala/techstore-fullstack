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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * JPA entity that represents a product available in the store catalog.
 *
 * <p>This class exists to hold the core data needed to sell an item, including its descriptive
 * information, pricing, stock, brand, category, and product images.
 *
 * <p>Architecturally, this entity belongs to the domain/persistence layer and maps the
 * {@code products} table while participating in several relationships that structure the catalog.
 *
 * <p>It is used whenever products are created, displayed, updated, or referenced by carts and
 * orders.
 */
// `@Entity` marks this class as a JPA-managed persistent entity.
@Entity
@Getter
@NoArgsConstructor
// `@Table` maps this entity explicitly to the `products` table.
@Table(name = "products")
public class Product {

    // Primary key that uniquely identifies a product row.
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private String description;

    // Many products can belong to the same brand.
    @ManyToOne
    @Setter //pick up later
    // `@JoinColumn` defines the foreign key column stored in the product table, which means this
    // entity owns the relationship to `Brand`.
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    // Many products can belong to the same category.
    @Setter // pick up later
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


    @Setter //pick up later
    // Precision and scale are specified to map money values safely at the database level.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;


    // The SKU is a business identifier used to uniquely distinguish products in stock/catalog flows.
    @Column(nullable = false, unique = true)
    private String sku;

    // Stock tracks how many units are available for sale.
    @Column(nullable = false)
    @Setter //pick up later
    private Integer stock;

    // A separate active flag allows products to be hidden or disabled without deleting data.
    @Column(nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One product can have multiple images.
    // `mappedBy = "product"` means `ProductImage` owns the relationship by storing the foreign key.
    // `cascade = CascadeType.ALL` keeps image persistence operations aligned with the parent product.
    // `orphanRemoval = true` deletes image rows that are removed from the collection.
    @OneToMany(mappedBy = "product", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List <ProductImage> images;


    
}
