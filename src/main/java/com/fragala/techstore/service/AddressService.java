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
import com.fragala.techstore.mapper.AddressMapper;

@Service
public class AddressService {
    

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository, AddressMapper addressMapper){
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.addressMapper = addressMapper;
    }
    public AddressResponse create(CreateAddressRequest request){

        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Address address = addressMapper.toEntity(request);

        address.setUser(user);

        address = addressRepository.save(address);

        return addressMapper.toResponse(address);
     }


    public List<AddressResponse> findAll(){

        List<Address> addresses = addressRepository.findAll();

        List<AddressResponse> responses = new ArrayList<>();

        for (Address address: addresses){

            responses.add(addressMapper.toResponse(address));
        }
        return responses;
    }
    public AddressResponse findById(Long id){
        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Address not found"));

        return addressMapper.toResponse(address);
    }
    
    }



