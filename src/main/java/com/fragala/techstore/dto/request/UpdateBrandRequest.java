package com.fragala.techstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor   
public class UpdateBrandRequest {

    @NotBlank 
    private String name;
    
}
