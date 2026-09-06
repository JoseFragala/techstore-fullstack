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

import com.fragala.techstore.dto.request.CreateCategoryRequest;
import com.fragala.techstore.dto.request.UpdateCategoryRequest;
import com.fragala.techstore.dto.response.CategoryResponse;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.service.CategoryService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import tools.jackson.databind.ObjectMapper;



@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private CategoryService categoryService;


    
    @Test // request valid
    void create_shouldReturn201_whenRequestIsValid() throws Exception {

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Laptops");


        CategoryResponse response = new CategoryResponse(1L, "Laptops");

        when(categoryService.create(any(CreateCategoryRequest.class)))
            .thenReturn(response);

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptops"));
        
    }
    @Test // Post with empty name
    void create_shouldReturn400_whenNameisBlank() throws Exception {

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("");

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test // Get founded id.
    void findById_shouldReturn200_whenCategoryExists() throws Exception {

        CategoryResponse response = new CategoryResponse(1L, "Laptops");

        when(categoryService.findById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptops"));
    }
    @Test // get not found id
    void findById_shouldReturn404_whenCategoryDoesNotExist() throws Exception {

        when(categoryService.findById(1L))
                .thenThrow(new ResourceNotFoundException("Category not Found"));

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isNotFound());
    }

    @Test //get all founded
    void findAll_shouldReturn200_withCategories() throws Exception {

        List<CategoryResponse> response = List.of(
                new CategoryResponse(1L, "Laptops"),
                new CategoryResponse(2L, "Smartphones")
        );

        when(categoryService.findAll())
            .thenReturn(response);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptops"))
                .andExpect(jsonPath("$[1].name").value("Smartphones"));

    }

    @Test//get all empty list
    void findAll_shouldReturn200_whenNoCategoriesExist() throws Exception {

        when(categoryService.findAll())
                .thenReturn(List.of());
            
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
            }

    @Test // put sucessfully upgrade
    void update_shouldReturn200_whenRequestIsValid() throws Exception {
    

        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("Laptops");

        CategoryResponse response = new CategoryResponse(1L, "Laptops");

        when(categoryService.update(eq(1L), any(UpdateCategoryRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptops"));
        
    }

    @Test // put with empty name
    void update_shouldReturn400_whenNameIsBlank() throws Exception {

        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("");

        mockMvc.perform(put("/categories/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test // Delete sucessfully
    void delete_shouldReturn204_whenCategoryExists() throws Exception {

        doNothing().when(categoryService).delete(1L);

        mockMvc.perform(delete("/categories/1"))
            .andExpect(status().isNoContent());

    }

    @Test// Delete id not found
    void delete_shouldReturn404_whenCategoryDoesExist() throws Exception {

        doThrow(new ResourceNotFoundException("Category not found"))
                .when(categoryService).delete(1L);
        
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNotFound());
    }



    


    }
    


