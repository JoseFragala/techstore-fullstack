package com.fragala.techstore.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
public class UpdateUserRequest {

     @NotBlank 
    private String name;

    @NotBlank 
    @Email 
    private String email;

    @NotBlank 
    private String phone;

    @NotNull 
    private Long roleId;

    
}
