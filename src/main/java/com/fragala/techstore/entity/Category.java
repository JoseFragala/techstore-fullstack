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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity that represents a product category in the catalog.
 *
 * <p>This class exists to organize products into meaningful groups such as laptops, accessories,
 * or smartphones. Modeling categories separately keeps the catalog structured and avoids repeating
 * plain text category names on every product row.
 *
 * <p>Architecturally, this entity belongs to the persistence/domain layer and maps the
 * {@code categories} table.
 *
 * <p>It is used whenever products need to be classified or queried by category.
 */
// `@Entity` indicates that this class should be persisted by JPA.
@Entity
@NoArgsConstructor
@Getter
// `@Table` explicitly maps the entity to the `categories` table.
@Table(name = "categories")
public class Category {

    // Primary key used to uniquely identify each category.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The name is unique to prevent multiple category rows describing the same category.
    @Setter
    @Column(nullable = false, unique = true)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One category can be linked to many products.
    // `mappedBy = "category"` means `Product` owns the relationship because the foreign key
    // column is stored on the product table.
    @OneToMany(mappedBy = "category")
    private List <Product> products;
    
}
