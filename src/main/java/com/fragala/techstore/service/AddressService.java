package com.fragala.techstore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fragala.techstore.repository.AddressRepository;
import com.fragala.techstore.repository.UserRepository;
import com.fragala.techstore.dto.request.CreateAddressRequest;
import com.fragala.techstore.dto.response.AddressResponse;
import com.fragala.techstore.entity.User;
import com.fragala.techstore.entity.Address;

@Service
public class AddressService {
    

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository){
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }
    public AddressResponse create(CreateAddressRequest request){

        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Address address = new Address();

        address.setName(request.getName());
        address.setStreet(request.getStreet());
        address.setNumber(request.getNumber());
        address.setComplement(request.getComplement());
        address.setNeighborhood(request.getNeighborhood());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setDefaultAddress(request.getDefaultAddress());

        address.setUser(user);

        address = addressRepository.save(address);

        return new AddressResponse(
                address.getId(),
                address.getName(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry(),
                address.isDefaultAddress()

        );
     }


    public List<AddressResponse> findAll(){

        List<Address> addresses = addressRepository.findAll();

        List<AddressResponse> responses = new ArrayList<>();

        for (Address address: addresses){

            AddressResponse response = new AddressResponse(
                address.getId(),
                address.getName(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry(),
                address.isDefaultAddress()
            );

            responses.add(response);
        }
        return responses;


    }


}
