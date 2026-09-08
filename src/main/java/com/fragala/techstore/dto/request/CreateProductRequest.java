package com.fragala.techstore.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
public class CreateProductRequest {

    @NotBlank 
    private String name;

    @NotBlank 
    private String description;

    @NotNull 
    private Long brandId;

    @NotNull 
    private Long categoryId;

    @NotNull 
    @DecimalMin(value ="0.00")
    private BigDecimal price;

    @NotBlank 
    private String sku;

    @NotNull 
    @Min(0)
    private Integer stock;

    @Valid 
    private List<CreateProductImageRequest> images;
}
