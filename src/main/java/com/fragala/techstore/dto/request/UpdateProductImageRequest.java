package com.fragala.techstore.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
public class UpdateProductImageRequest{

    @NotBlank 
    private String imageUrl;

    @NotNull 
    @Min(1)
    private Integer displayOrder;


    
}
