package com.fragala.techstore.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import com.fragala.techstore.dto.request.CreateProductRequest;
import com.fragala.techstore.dto.response.ProductResponse;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.dto.response.BrandResponse;
import com.fragala.techstore.dto.response.CategoryResponse;
import com.fragala.techstore.service.ProductService;

import tools.jackson.databind.ObjectMapper;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fragala.techstore.dto.request.UpdateProductRequest;


@WebMvcTest(ProductController.class)
public class ProductControllerTest {
    
  @Autowired 
  private MockMvc mockMvc;

  @Autowired 
  private ObjectMapper objectMapper;

  @MockitoBean 
  private ProductService productService;

  @Test // post request valid
  void create_shouldReturnCreated_whenRequestIsValid() throws Exception {

    //GIVEN
    CreateProductRequest request = new CreateProductRequest();
        request.setName("Gaming Mouse");
        request.setDescription("Wireless gaming mouse");
        request.setBrandId(1L);
        request.setCategoryId(2L);
        request.setPrice(new BigDecimal("299.90"));
        request.setSku("MOUSE-001");
        request.setStock(10);
        request.setImages(List.of());

        ProductResponse response = new ProductResponse(
                1L,
                "Gaming Mouse",
                "Wireless gaming mouse",
                null,
                null,
                new BigDecimal("299.90"),
                "MOUSE-001",
                10,
                true,
                null,
                null,
                List.of()
        );

        when(productService.create(any(CreateProductRequest.class)))
                .thenReturn(response);

        // WHEN + THEN
        mockMvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gaming Mouse"))
                .andExpect(jsonPath("$.sku").value("MOUSE-001"))
                .andExpect(jsonPath("$.price").value(299.90))
                .andExpect(jsonPath("$.stock").value(10))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test // request invalid
    void create_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {

        // GIVEN
        CreateProductRequest request = new CreateProductRequest();
        request.setName("");
        request.setDescription("Wireless gaming mouse");
        request.setBrandId(1L);
        request.setCategoryId(2L);
        request.setPrice(new BigDecimal("299.90"));
        request.setSku("MOUSE-001");
        request.setStock(10);
        request.setImages(List.of());

        // WHEN + THEN
        mockMvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isBadRequest());
    }

    @Test // valid request.
    void update_shouldReturnOk_whenRequestIsValid() throws Exception {

            Long productId = 1L;

            UpdateProductRequest request = new UpdateProductRequest();
            request.setName("Updated Product");
            request.setDescription("Updated description");
            request.setBrandId(1L);
            request.setCategoryId(1L);
            request.setPrice(new BigDecimal("199.90"));
            request.setStock(20);

            ProductResponse response = new ProductResponse(
                    productId,
                    "Updated Product",
                    "Updated description",
                    new BrandResponse(1L, "Brand"),
                    new CategoryResponse(1L, "Category"),
                    new BigDecimal("199.90"),
                    "SKU-001",
                    20,
                    true,
                    null,
                    null,
                    java.util.List.of()
            );

            when(productService.update(eq(productId), any(UpdateProductRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(
                    put("/products/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Updated Product"))
                    .andExpect(jsonPath("$.description").value("Updated description"))
                    .andExpect(jsonPath("$.price").value(199.90))
                    .andExpect(jsonPath("$.sku").value("SKU-001"))
                    .andExpect(jsonPath("$.stock").value(20))
                    .andExpect(jsonPath("$.active").value(true));
        }

        @Test //invalid request.
        void update_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {

                Long productId = 1L;

                UpdateProductRequest request = new UpdateProductRequest();

                request.setName("");
                request.setDescription("Updated description");
                request.setBrandId(1L);
                request.setCategoryId(1L);
                request.setPrice(new BigDecimal("199.90"));
                request.setStock(20);

                mockMvc.perform(
                        put("/products/{id}", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                        .andExpect(status().isBadRequest());
            }
            @Test // findById product exists.
            void findById_shouldReturnOk_whenProductExists() throws Exception {
                Long productId = 1L;

                ProductResponse response = new ProductResponse(
                    productId,
                    "Laptop",
                    "Gaming laptop",
                    new BrandResponse(1L, "Brand"),
                    new CategoryResponse(1L, "Category"),
                    new BigDecimal("4999.90"),
                    "LAP-001",
                    10,
                    true,
                    null,
                    null,
                    java.util.List.of()
                );

                when(productService.findById(productId))
                    .thenReturn(response);

                mockMvc.perform(
                    get("/products/{id}", productId)
                )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Laptop"))
                    .andExpect(jsonPath("$.description").value("Gaming laptop"))
                    .andExpect(jsonPath("$.price").value(4999.90))
                    .andExpect(jsonPath("$.sku").value("LAP-001"))
                    .andExpect(jsonPath("$.stock").value(10))
                    .andExpect(jsonPath("$.active").value(true));

                verify(productService).findById(productId);
            }

            @Test // findbyid whem productdoe nos exists.
            void findById_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
                Long productId = 999L;

                when(productService.findById(productId))
                    .thenThrow(new ResourceNotFoundException("Product not found"));

                mockMvc.perform(
                    get("/products/{id}", productId)
                )
                    .andExpect(status().isNotFound());

                verify(productService).findById(productId);
            }

            @Test // find all product exists.
            void findAll_shouldReturnOk_whenProductsExist() throws Exception {

                ProductResponse firstProduct = new ProductResponse(
                    1L,
                    "Laptop",
                    "Gaming laptop",
                    new BrandResponse(1L, "Brand"),
                    new CategoryResponse(1L, "Category"),
                    new BigDecimal("4999.90"),
                    "LAP-001",
                    10,
                    true,
                    null,
                    null,
                    java.util.List.of()
                );

                ProductResponse secondProduct = new ProductResponse(
                    2L,
                    "Keyboard",
                    "Mechanical keyboard",
                    new BrandResponse(1L, "Brand"),
                    new CategoryResponse(2L, "Accessories"),
                    new BigDecimal("299.90"),
                    "KEY-001",
                    20,
                    true,
                    null,
                    null,
                    java.util.List.of()
                );

                when(productService.findAll())
                    .thenReturn(java.util.List.of(firstProduct, secondProduct));

                mockMvc.perform(
                    get("/products")
                )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("Laptop"))
                    .andExpect(jsonPath("$[0].sku").value("LAP-001"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].name").value("Keyboard"))
                    .andExpect(jsonPath("$[1].sku").value("KEY-001"));

                verify(productService).findAll();
            }

            @Test //emptylist
            void findAll_shouldReturnEmptyList_whenNoProductsExist() throws Exception {

                when(productService.findAll())
                    .thenReturn(java.util.List.of());

                mockMvc.perform(
                    get("/products")
                )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));

                verify(productService).findAll();
            }
            @Test // deactivate sucess
            void deactivate_shouldReturnNoContent_whenProductExists() throws Exception {
                Long productId = 1L;

                doNothing().when(productService).deactivate(productId);

                mockMvc.perform(patch("/products/{id}/deactivate", productId))
                        .andExpect(status().isNoContent());

                verify(productService).deactivate(productId);
            }
            
            @Test //deactivate product not found.
            void deactivate_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
                Long productId = 1L;

                doThrow(new ResourceNotFoundException("Product not found"))
                        .when(productService)
                        .deactivate(productId);

                mockMvc.perform(patch("/products/{id}/deactivate", productId))
                        .andExpect(status().isNotFound());

                verify(productService).deactivate(productId);
            }
            @Test //activate sucess
            void activate_shouldReturnNoContent_whenProductExists() throws Exception {
                Long productId = 1L;

                doNothing().when(productService).activate(productId);

                mockMvc.perform(patch("/products/{id}/activate", productId))
                        .andExpect(status().isNoContent());

                verify(productService).activate(productId);

            }
            @Test //activate - not found
            void activate_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
                Long productId = 1L;

                doThrow(new ResourceNotFoundException("Product not found"))
                        .when(productService)
                        .activate(productId);

                mockMvc.perform(patch("/products/{id}/activate", productId))
                        .andExpect(status().isNotFound());

                verify(productService).activate(productId);
            }

            @Test // sucessfully delete
            void delete_shouldReturnNoContent_whenProductExists() throws Exception {
                Long productId = 1L;

                doNothing().when(productService).delete(productId);

                mockMvc.perform(delete("/products/{id}", productId))
                        .andExpect(status().isNoContent());

                verify(productService).delete(productId);
            }
            @Test // product not found
            void delete_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
                Long productId = 1L;

                doThrow(new ResourceNotFoundException("Product not found"))
                        .when(productService)
                        .delete(productId);

                mockMvc.perform(delete("/products/{id}", productId))
                        .andExpect(status().isNotFound());

                verify(productService).delete(productId);
            }




            
}
  