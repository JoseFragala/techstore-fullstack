package com.fragala.techstore.entity;

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

/**
 * JPA entity that represents an image associated with a product.
 *
 * <p>This class exists because products often need multiple images, and each image may carry
 * additional metadata such as display order. A dedicated entity keeps that data organized.
 *
 * <p>Architecturally, this entity belongs to the persistence/domain layer and maps the
 * {@code product_images} table as a child of {@link Product}.
 *
 * <p>It is used whenever the catalog needs to store or display product gallery images.
 */
// `@Entity` tells JPA to persist this class.
@Entity
@Getter
// `@Table` maps the entity to the `product_images` table.
@Table(name = "product_images")
public class ProductImage {

    // Primary key that uniquely identifies each image row.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many images can belong to one product because a product can have a gallery of images.
    @ManyToOne
    // This entity owns the relationship because the `product_id` foreign key is stored here.
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Stores the location of the image asset that should be displayed for the product.
    @Column(nullable = false)
    private String imageUrl;

    // Defines the presentation order so the UI can show images consistently.
    @Column(nullable = false) //order to show the images.
    private Integer displayOrder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
