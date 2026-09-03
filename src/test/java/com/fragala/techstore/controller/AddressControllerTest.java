package com.fragala.techstore.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fragala.techstore.dto.request.CreateAddressRequest;
import com.fragala.techstore.dto.request.UpdateAddressRequest;
import com.fragala.techstore.dto.response.AddressResponse;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.service.AddressService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;


@WebMvcTest(AddressController.class)
public class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;

    @Test //Test endponti GET/ Adresses/{id}
    void shouldReturnAddressById() throws Exception {

        //GIVEN

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
        
        when(addressService.findById(1L))
            .thenReturn(response);

        //WHEN + THEN

        mockMvc.perform(get("/addresses/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Home"));

            
    }

    @Test // Address not found
    void shouldReturn404WhenAddressDoesNotExist() throws Exception {

        // GIVEN

        when(addressService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Address not found"));
        
        //WHEN + THEN

        mockMvc.perform(get("/addresses/999")) 
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Address not found"));
      
                
    }

    @Test // GET/addresses
    void shouldReturnAllAddresses() throws Exception {
        // GIVEN

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

    
    when(addressService.findAll())
            .thenReturn(List.of(response1, response2));

    //WHEN + THEN 

    mockMvc.perform(get("/addresses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Home"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Work"));
    }

    @Test //POST / ADDRESS - VALID REQUEST
    void shouldCreateAddress() throws Exception {

        // GIVEN

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

    when(addressService.create(any(CreateAddressRequest.class)))
            .thenReturn(response);

      String json = """
            {
                "name": "Home",
                "street": "Main Street",
                "number": "100",
                "complement": "Apartment 5",
                "neighborhood": "Downtown",
                "city": "New York",
                "state": "NY",
                "zipCode": "10001",
                "country": "USA",
                "defaultAddress": true,
                "userId": 2
            }
            """;
        // WHEN + THEN

        mockMvc.perform(
            post("/addresses")
                    .contentType("application/json")
                    .content(json)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Home"));
    }

    @Test //Post /addresses with invalid data
    void shouldReturn400WhenCreateAddressRequestIsInvalid() throws Exception {

        //GIVEN

        String json = """
            {
                "name": "",
                "street": "",
                "number": "",
                "complement": "",
                "neighborhood": "",
                "city": "",
                "state": "",
                "zipCode": "",
                "country": "",
                "defaultAddress": null,
                "userId": null
            }
            """;

        // WHEN + THEN

        mockMvc.perform(
                post("/addresses")
                        .contentType("application/json")
                        .content(json)
        )

            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.errors.name").exists())
            .andExpect(jsonPath("$.errors.street").exists())
            .andExpect(jsonPath("$.errors.city").exists())
            .andExpect(jsonPath("$.errors.defaultAddress").exists())
            .andExpect(jsonPath("$.errors.userId").exists());
    }

    @Test // PUT / addresses/{id}
    void shouldUpdateAddress() throws Exception {

        //GIVEN
           AddressResponse response = new AddressResponse(
            1L,
            "Updated Home",
            "New Street",
            "200",
            "Apartment 10",
            "Downtown",
            "New York",
            "NY",
            "10001",
            "USA",
            true
    );

    when(addressService.update(
            org.mockito.ArgumentMatchers.eq(1L),
            any(UpdateAddressRequest.class)
    )).thenReturn(response);

    String json = """
            {
                "name": "Updated Home",
                "street": "New Street",
                "number": "200",
                "complement": "Apartment 10",
                "neighborhood": "Downtown",
                "city": "New York",
                "state": "NY",
                "zipCode": "10001",
                "country": "USA",
                "defaultAddress": true
            }
            """;

        //WHEN + THEN

        mockMvc.perform(
            put("/addresses/1")
                    .contentType("application/json")
                    .content(json)
        )     
            
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Home"))
                .andExpect(jsonPath("$.street").value("New Street"));

    }

    @Test // PUT /addresses/{id} - invalid
    void shouldReturn400WhenUpdateAddressRequestIsInvalid() throws Exception {

        // GIVEN

          String json = """
            {
                "name": "",
                "street": "",
                "number": "",
                "complement": "",
                "neighborhood": "",
                "city": "",
                "state": "",
                "zipCode": "",
                "country": "",
                "defaultAddress": null
            }
            """;

    // WHEN + THEN

    mockMvc.perform(
            put("/addresses/1")
                    .contentType("application/json")
                    .content(json)
    )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.errors.name").exists())
            .andExpect(jsonPath("$.errors.street").exists())
            .andExpect(jsonPath("$.errors.number").exists())
            .andExpect(jsonPath("$.errors.city").exists())
            .andExpect(jsonPath("$.errors.defaultAddress").exists());

    verify(addressService, never())
            .update(eq(1L), any(UpdateAddressRequest.class));
    }    

    @Test // PUT - address dosent exist
    void shouldReturn404WhenUpdatingAddressDoesNotExist() throws Exception {

    // GIVEN

    when(addressService.update(eq(999L), any(UpdateAddressRequest.class)))
            .thenThrow(new ResourceNotFoundException("Address not found"));

    String json = """
            {
                "name": "Home",
                "street": "Main Street",
                "number": "100",
                "complement": "Apartment 5",
                "neighborhood": "Downtown",
                "city": "New York",
                "state": "NY",
                "zipCode": "10001",
                "country": "USA",
                "defaultAddress": true
            }
            """;

    // WHEN + THEN

    mockMvc.perform(
            put("/addresses/999")
                    .contentType("application/json")
                    .content(json)
    )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("Address not found"));

    }

    @Test //successful  DELETE
    void shouldDeleteAddress() throws Exception {

        //WHEN + THEN

        mockMvc.perform(delete("/addresses/1"))
                .andExpect(status().isNoContent());
        
        verify(addressService).delete(1L);
    }

    @Test // address doest exist DELETE
    void shouldReturn404WhenDeletingAddressDoesNotExist () throws Exception {

        // GIVEN

        doThrow(new ResourceNotFoundException("Address not found"))
            .when(addressService)
            .delete(999L);
        
        // WHEN + THEN

        mockMvc.perform(delete("/addresses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Address not found"));
    }








}