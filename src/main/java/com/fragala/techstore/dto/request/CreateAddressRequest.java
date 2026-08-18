package com.fragala.techstore.dto.request;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO used when the client wants to create a new address.
 * iT contains only the information the API accepts.
 * CreateAddressRequest
 */
@Getter
@NoArgsConstructor
public class CreateAddressRequest {

    @NotBlank   
    private String name;
    @NotBlank
    private String street;
    @NotBlank
    private String number;
    private String complement;
    @NotBlank
    private String neighborhood;
    @NotBlank
    private String city;
    @NotBlank
    private String state;
    @NotBlank
    private String zipCode;
    @NotBlank
    private String country;
    @NotNull
    private Boolean defaultAddress;
    @NotNull
    private Long userId;
    
}