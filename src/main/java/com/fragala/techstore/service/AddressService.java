package com.fragala.techstore.service;

import org.springframework.stereotype.Service;

import com.fragala.techstore.repository.AddressRepository;
import com.fragala.techstore.repository.UserRepository;

@Service
public class AddressService {
    

    private final AddressRepository addressRepository;
    private final UserRepository UserRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository){
        this.addressRepository = addressRepository;
        this.UserRepository = userRepository;
    }
}


