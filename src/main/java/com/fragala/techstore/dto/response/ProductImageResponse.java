package com.fragala.techstore.dto.response;

import lombok.Getter;

@Getter 
public class ProductImageResponse {
    
    private Long id;
    private String imageUrl;
    private Integer displayOrder;


    public ProductImageResponse(
        Long id,
        String imageUrl,
        Integer displayOrder) {

            this.id = id;
            this.imageUrl = imageUrl;
            this.displayOrder = displayOrder;
        }
    }

