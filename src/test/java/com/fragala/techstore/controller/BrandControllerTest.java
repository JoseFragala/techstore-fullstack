package com.fragala.techstore.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fragala.techstore.dto.request.CreateBrandRequest;
import com.fragala.techstore.dto.request.UpdateBrandRequest;
import com.fragala.techstore.dto.response.BrandResponse;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.service.BrandService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(BrandController.class)
public class BrandControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private BrandService brandService;


    
    @Test // request valid
    void create_shouldReturn201_whenRequestIsValid() throws Exception {

        CreateBrandRequest request = new CreateBrandRequest();
        request.setName("Samsung");


        BrandResponse response = new BrandResponse(1L, "Samsung");

        when(brandService.create(any(CreateBrandRequest.class)))
            .thenReturn(response);

        mockMvc.perform(post("/brand")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Samsung"));
        
    }
    @Test // Post with empty name
    void create_shouldReturn400_whenNameisBlank() throws Exception {

        CreateBrandRequest request = new CreateBrandRequest();
        request.setName("");

        mockMvc.perform(post("/brand")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test // Get founded id.
    void findById_shouldReturn200_whenBrandExists() throws Exception {

        BrandResponse response = new BrandResponse(1L, "Samsung");

        when(brandService.findById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/brand/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Samsung"));
    }
    @Test // get not found id
    void findById_shouldReturn404_whenBrandDoesNotExist() throws Exception {

        when(brandService.findById(1L))
                .thenThrow(new ResourceNotFoundException("Brand not Found"));

        mockMvc.perform(get("/brand/1"))
                .andExpect(status().isNotFound());
    }

    @Test //get all founded
    void findAll_shouldReturn200_withBrand() throws Exception {

        List<BrandResponse> response = List.of(
                new BrandResponse(1L, "Samsung"),
                new BrandResponse(2L, "Apple")
        );

        when(brandService.findAll())
            .thenReturn(response);

        mockMvc.perform(get("/brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Samsung"))
                .andExpect(jsonPath("$[1].name").value("Apple"));

    }

    @Test//get all empty list
    void findAll_shouldReturn200_whenNoBrandExist() throws Exception {

        when(brandService.findAll())
                .thenReturn(List.of());
            
        mockMvc.perform(get("/brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
            }

    @Test // put sucessfully upgrade
    void update_shouldReturn200_whenRequestIsValid() throws Exception {
    

        UpdateBrandRequest request = new UpdateBrandRequest();
        request.setName("Samsung");

        BrandResponse response = new BrandResponse(1L, "Samsung");

        when(brandService.update(eq(1L), any(UpdateBrandRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/brand/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Samsung"));
        
    }

    @Test // put with empty name
    void update_shouldReturn400_whenNameIsBlank() throws Exception {

        UpdateBrandRequest request = new UpdateBrandRequest();
        request.setName("");

        mockMvc.perform(put("/brand/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test // Delete sucessfully
    void delete_shouldReturn204_whenBrandExists() throws Exception {

        doNothing().when(brandService).delete(1L);

        mockMvc.perform(delete("/brand/1"))
            .andExpect(status().isNoContent());

    }

    @Test// Delete id not found
    void delete_shouldReturn404_whenBrandDoesExist() throws Exception {

        doThrow(new ResourceNotFoundException("Brand not found"))
                .when(brandService).delete(1L);
        
        mockMvc.perform(delete("/brand/1"))
                .andExpect(status().isNotFound());
    }



    


    }
    


