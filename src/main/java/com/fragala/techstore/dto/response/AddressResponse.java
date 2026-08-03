package com.fragala.techstore.dto.response;

import lombok.Getter;

@Getter
public class AddressResponse {

    private Long id;

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

    public AddressResponse(
        Long id,
        String name,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        String country,
        Boolean defaultAddress){
    
    this.id = id; 
    this.name = name;
    this.street = street;
    this.number = number;
    this.complement = complement;
    this.neighborhood = neighborhood;
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
    this.country = country;
    this.defaultAddress = defaultAddress;
        }
}

    

