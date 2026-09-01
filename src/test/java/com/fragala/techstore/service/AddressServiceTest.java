package com.fragala.techstore.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fragala.techstore.dto.request.CreateAddressRequest;
import com.fragala.techstore.dto.request.UpdateAddressRequest;
import com.fragala.techstore.dto.response.AddressResponse;
import com.fragala.techstore.entity.Address;
import com.fragala.techstore.entity.User;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.mapper.AddressMapper;
import com.fragala.techstore.repository.AddressRepository;
import com.fragala.techstore.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    @Test //test to findbyid
    void shouldFindAddressById(){

        //GIVEN
        Address address = new Address();

        AddressResponse response = new AddressResponse( 
            1L,
            "Home",
            "Main Street",
            "100",
            "Apartment 5",
            "Downtown",
            "New York",
            "NY",
            "10001",
            "USA",
            true
    );

        when(addressRepository.findById(1L))
            .thenReturn(Optional.of(address));
        
        when(addressMapper.toResponse(address))
            .thenReturn(response);

    //WHEN
    AddressResponse result = addressService.findById(1L);


    //THEN
    assertEquals(1L, result.getId());
    assertEquals("Home", result.getName());


    }

    @Test // test to failure case "Address not found"
    void shouldThrowExceptionWhenAddressDoesNotExist(){

    //GIVEN
    when(addressRepository.findById(999L))
        .thenReturn(Optional.empty());

    // WHEN + THEN

    assertThrows(
        ResourceNotFoundException.class,
        () -> addressService.findById(999L)
    );
    }

    @Test // Test the default-address business rule
    void shouldChangePreviousDefaultAddressWhenUpdatingToDefault(){

        //GIVEN 

        Address address = new Address();

        User user = mock(User.class);

        when(user.getId()).thenReturn(2L);

        address.setUser(user);

        Address oldDefault = mock(Address.class);

        when(oldDefault.getId()).thenReturn(1L);


        when(addressRepository.findById(2L))
            .thenReturn(Optional.of(address));
        
        when(addressRepository.findByUser_IdAndDefaultAddressTrue(2L))
                .thenReturn(Optional.of(oldDefault));

        UpdateAddressRequest request = new UpdateAddressRequest(
            "Home",
            "Main Street",
            "100",
            "Apartment 5",
            "Downtown",
            "New York",
            "NY",
            "10001",
            "USA",
            true
    );

    AddressResponse response = new AddressResponse(    2L,
            "Home",
            "Main Street",
            "100",
            "Apartment 5",
            "Downtown",
            "New York",
            "NY",
            "10001",
            "USA",
            true
    ); 

        when(addressRepository.save(address))
            .thenReturn(address);
        
        when(addressMapper.toResponse(address))
            .thenReturn(response);

    // WHEN
    addressService.update(2L, request);

    //THEN
    
    verify(oldDefault).setDefaultAddress(false);

    verify(addressRepository).saveAndFlush(oldDefault);
    }

    @Test // If current address is already the default.
    void shouldNotChangeDefaultAddressWhenUpdatingCurrentDefault() {
        //GIVEN

        Address address = mock(Address.class);

        User user = mock(User.class);

        when(user.getId()).thenReturn(2L);
        when(address.getId()).thenReturn(1L);
        when(address.getUser()).thenReturn(user);

        when(addressRepository.findById(1L))
            .thenReturn(Optional.of(address));

        // The current default is the SAME address we're updating
        when(addressRepository.findByUser_IdAndDefaultAddressTrue(2L))
            .thenReturn(Optional.of(address));
        
        UpdateAddressRequest request = new UpdateAddressRequest(
            "Home",
            "Main Street",
            "100",
            "Apartment 5",
            "Downtown",
            "New York",
            "NY",
            "10001",
            "USA",
            true
    );

    AddressResponse response = new AddressResponse(
        1L,
            "Home",
            "Main Street",
            "100",
            "Apartment 5",
            "Downtown",
            "New York",
            "NY",
            "10001",
            "USA",
            true
    );
    
    when(addressRepository.save(address))
        .thenReturn(address);
    
    when(addressMapper.toResponse(address))
        .thenReturn(response);


    // WHEN

    addressService.update(1L, request);

    //THEN

    verify(addressRepository, never())
        .saveAndFlush(address);
        
    }
    @Test // create address (user doesnt exists - ResourceNotFoundException)
    void shouldThrowExceptionUserDoesNotExist() {

        //GIVEN
        CreateAddressRequest request = mock(CreateAddressRequest.class);

        when(request.getUserId()).thenReturn(999L);

        when(userRepository.findById(999L))
            .thenReturn(Optional.empty());
        
        //WHEN + THEN
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> addressService.create(request)
        );

        assertEquals("User not found", exception.getMessage());

        verify(addressMapper, never()).toEntity(request);
    }
    @Test // create address (user exists - address is created)
    void shouldCreateAddress(){

        //GIVEN
        CreateAddressRequest request = mock(CreateAddressRequest.class);

        User user = mock(User.class);

        Address address = new Address();

        AddressResponse response = new AddressResponse(
            1L,
            "Home",
            "Main Street",
            "100",
            "Apartment 5",
            "Downtown",
            "New York",
            "NY",
            "10001",
            "USA",
            true
        );

        when(request.getUserId()).thenReturn(2L);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));
        
        when(addressMapper.toEntity(request))
            .thenReturn(address);
        
        when(addressRepository.save(address))
            .thenReturn(address);
        
        when(addressMapper.toResponse(address))
            .thenReturn(response);
        
        
        //WHEN

        AddressResponse result = addressService.create(request);

        //THEN

        assertEquals(1L, result.getId());
        assertEquals(1L, result.getId());
        assertEquals("Home", result.getName());

        verify(addressMapper).toEntity(request);
        verify(addressRepository).save(address);

    }

    @Test // Delete - address exists
    void shouldDeleteAddress() {

        //GIVEN
        Address address = new Address ();

        when (addressRepository.findById(1L))
            .thenReturn(Optional.of(address));
        
        // WHEN
        addressService.delete(1L);

        //THEN
        verify(addressRepository).delete(address);

    }
    @Test // Delete - address doesnt exists - throw exception
    void shouldThrowExceptionWhenDeletingAddressDoesNotExist(){

        //GIVEN
        when(addressRepository.findById(999L))
            .thenReturn(Optional.empty());
        
        // WHEN + THEN
        assertThrows(
            ResourceNotFoundException.class,
            () -> addressService.delete(999L)
        );

        verify(addressRepository, never()).delete(any(Address.class));
       
        }

    @Test // FindAll
    void shouldFindAllAddresses(){

        //GIVEN
        Address address1 = new Address();
        Address address2 = new Address();
        Address address3 = new Address();

        AddressResponse response1 = new AddressResponse(
            1L,
            "Home",
            "Main Street",
            "100",
            "Apartment 5",
            "Downtown",
            "New York",
            "NY",
            "10001",
            "USA",
            true
    );

    AddressResponse response2 = new AddressResponse(
            2L,
            "Work",
            "Second Street",
            "200",
            "Room 10",
            "Downtown",
            "New York",
            "NY",
            "10001",
            "USA",
            false
    );

    AddressResponse response3 = new AddressResponse(
            3L,
            "Other",
            "Third Street",
            "300",
            null,
            "Downtown",
            "New York",
            "NY",
            "10001",
            "USA",
            false
    );

    when(addressRepository.findAll())
        .thenReturn(List.of(address1, address2, address3));

    when(addressMapper.toResponse(address1))
        .thenReturn(response1);
    
    when(addressMapper.toResponse(address2))
        .thenReturn(response2);
    
    when(addressMapper.toResponse(address3))
        .thenReturn(response3);


    //WHEN

    List<AddressResponse> result = addressService.findAll();

    //THEN

    assertEquals(3, result.size());

    assertEquals("Home", result.get(0).getName());
    assertEquals("Work", result.get(1).getName());
    assertEquals("Other", result.get(2).getName());

    verify(addressMapper).toResponse(address1);
    verify(addressMapper).toResponse(address2);
    verify(addressMapper).toResponse(address3);
    }







    
    
}

