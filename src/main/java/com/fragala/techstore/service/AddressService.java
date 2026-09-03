package com.fragala.techstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fragala.techstore.repository.AddressRepository;
import com.fragala.techstore.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.fragala.techstore.dto.request.CreateAddressRequest;
import com.fragala.techstore.dto.request.UpdateAddressRequest;
import com.fragala.techstore.dto.response.AddressResponse;
import com.fragala.techstore.entity.User;
import com.fragala.techstore.exception.ResourceNotFoundException;
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
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Address address = addressMapper.toEntity(request);

        address.setUser(user);

        if (request.getDefaultAddress()) {

            Optional<Address> currentDefault = 
                addressRepository.findByUser_IdAndDefaultAddressTrue(
                    address.getUser().getId());

            if (currentDefault.isPresent()) {
                Address oldDefault = currentDefault.get();
                oldDefault.setDefaultAddress(false);

                addressRepository.saveAndFlush(oldDefault);
            }
        }       

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
            .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        return addressMapper.toResponse(address);
    }

    @Transactional
    public AddressResponse update(Long id, UpdateAddressRequest request){

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (request.getDefaultAddress()) {

            Optional<Address> currentDefault = 
                addressRepository.findByUser_IdAndDefaultAddressTrue(
                        address.getUser().getId());
                
                if (currentDefault.isPresent()
                    && !currentDefault.get().getId().equals(address.getId())) {
                
                Address oldDefault = currentDefault.get();

                oldDefault.setDefaultAddress(false);

                addressRepository.saveAndFlush(oldDefault); // save it and immediately synchronize this change with the database.
                    }
        }

        addressMapper.updateEntity(address, request);

        address = addressRepository.save(address);

        return addressMapper.toResponse(address);
    }

    public void delete (Long id) {

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        addressRepository.delete(address);
    }

    
    }



