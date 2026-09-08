package com.fragala.techstore.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;

@Getter 
public class ProductResponse {
    

    private Long id;
    private String name;
    private String description;
    private BrandResponse brand;
    private CategoryResponse category;
    private BigDecimal price;
    private String sku;
    private Integer stock;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProductImageResponse> images;

    public ProductResponse(
        Long id,
        String name,
        String description,
        BrandResponse brand,
        CategoryResponse category,
        BigDecimal price,
        String sku,
        Integer stock,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ProductImageResponse> images){

            this.id = id;
            this.name = name;
            this.description = description;
            this.brand = brand;
            this.category = category;
            this.price = price;
            this.sku = sku;
            this.stock = stock;
            this.active = active;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.images = images;
        }
}
    
    

