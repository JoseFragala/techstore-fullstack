package com.fragala.techstore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fragala.techstore.dto.request.CreateAddressRequest;
import com.fragala.techstore.dto.request.UpdateAddressRequest;
import com.fragala.techstore.dto.response.AddressResponse;
import com.fragala.techstore.service.AddressService;

import jakarta.validation.Valid;

@RestController // responsible for receiving HTTP request and returning HTTP response
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService){
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressResponse> create(@Valid @RequestBody CreateAddressRequest request){

        AddressResponse response = addressService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> findAll(){

        List<AddressResponse> responses = addressService.findAll();

        return ResponseEntity.ok(responses);
        
    }
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> findById(@PathVariable Long id){

        AddressResponse response = addressService.findById(id);

        return ResponseEntity.ok(response);

    }
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateAddressRequest request) {

            AddressResponse response = addressService.update(id, request);

            return ResponseEntity.ok(response);

        }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){

        addressService.delete(id);

        return ResponseEntity.noContent().build();
    }
    






    
}
