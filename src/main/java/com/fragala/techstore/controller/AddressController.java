package com.fragala.techstore.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fragala.techstore.dto.request.CreateAddressRequest;
import com.fragala.techstore.dto.response.AddressResponse;
import com.fragala.techstore.service.AddressService;

@RestController // responsible for receiving HTTP request and returning HTTP response
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService){
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressResponse> create(@RequestBody CreateAddressRequest request){

        AddressResponse response = addressService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }





    
}
