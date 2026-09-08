package com.fragala.techstore.dto.request;

import java.math.BigDecimal;



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
public class UpdateProductRequest {
    
    @NotBlank 
    private String name;    


    @NotBlank 
    private String description;

    @NotNull 
    private Long brandId;   

    @NotNull 
    private Long categoryId;

    @NotNull 
    @DecimalMin(value = "0.00")
    private BigDecimal price;

    @NotNull 
    @Min(0)
    private Integer stock;

}
