package com.fragala.techstore.dto.request;

/**
 * DTO used when the client wants to create a new address.
 * iT contains only the information the API accepts.
 * CreateAddressRequest
 */

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
    private Boolean isDefault;
    private Long userId;
    
    public String getName() {
        return name;
    }
    public String getStreet() {
        return street;
    }
    public String getNumber() {
        return number;
    }
    public String getComplement() {
        return complement;
    }
    public String getNeighborhood() {
        return neighborhood;
    }
    public String getCity() {
        return city;
    }
    public String getState() {
        return state;
    }
    public String getZipCode() {
        return zipCode;
    }
    public String getCountry() {
        return country;
    }
    public Boolean getIsDefault() {
        return isDefault;
    }
    public Long getUserId() {
        return userId;
    }
}
