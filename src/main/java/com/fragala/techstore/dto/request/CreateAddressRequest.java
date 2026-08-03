package com.fragala.techstore.dto.request;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO used when the client wants to create a new address.
 * iT contains only the information the API accepts.
 * CreateAddressRequest
 */
@Getter
@NoArgsConstructor
public class CreateAddressRequest {


    private String name;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private Boolean defaultAddress;
    private Long userId;
    
}