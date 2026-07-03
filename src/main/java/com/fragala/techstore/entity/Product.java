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


@Entity
@Getter
@NoArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private String description;

    @ManyToOne
    @Setter //pick up later
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Setter // pick up later
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


    @Setter //pick up later
    @Column(nullable = false)
    private BigDecimal price;


    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    @Setter //pick up later
    private Integer stock;

    @Column(nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column (nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column (nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product")
    private List <ProductImage> images;


    
}
