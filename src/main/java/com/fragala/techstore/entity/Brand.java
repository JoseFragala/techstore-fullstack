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
 * JPA entity that represents a product brand in the catalog.
 *
 * <p>This class exists to model the manufacturer or commercial brand associated with products.
 * Separating brand information into its own entity avoids duplication and makes the catalog more
 * normalized.
 *
 * <p>Within the architecture, this entity belongs to the domain/persistence layer and maps the
 * {@code brands} table.
 *
 * <p>It is used whenever products need to be grouped by brand or when brand metadata must be
 * stored independently from the product itself.
 */
// `@Entity` makes this class a managed JPA entity so it can be persisted in the database.
@Entity
@Getter
@NoArgsConstructor
// `@Table` specifies the database table name for this entity.
@Table (name = "brands")
public class Brand {

    // Primary key used to uniquely identify each brand record.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A unique brand name prevents duplicate brand rows that would represent the same concept.
    @Setter
    @Column(nullable = false, unique = true)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // `@OneToMany` models the inverse side of the relationship: one brand can be referenced by
    // many products.
    // `mappedBy = "brand"` means the `Product` entity owns the relationship because it contains
    // the foreign key column. This side is read as a convenience view of related products.
    @OneToMany(mappedBy = "brand")
    private List <Product> products;






    
}
