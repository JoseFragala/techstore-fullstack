package com.fragala.techstore.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(


    @NotBlank 
    String name,

    @NotBlank 
    @Email 
    String email,

    @NotBlank 
    String password,

    @NotBlank 
    String phone

) {
}
