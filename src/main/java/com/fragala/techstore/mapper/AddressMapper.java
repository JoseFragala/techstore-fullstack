package com.fragala.techstore.mapper;

import org.springframework.stereotype.Component;

import com.fragala.techstore.dto.request.CreateAddressRequest;
import com.fragala.techstore.dto.request.UpdateAddressRequest;
import com.fragala.techstore.dto.response.AddressResponse;
import com.fragala.techstore.entity.Address;

@Component
public class AddressMapper {
    
    public AddressResponse toResponse(Address address){

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
        public Address toEntity(CreateAddressRequest request){

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

                return address;
        }

        public void updateEntity (Address address, UpdateAddressRequest request){
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
        }
}


