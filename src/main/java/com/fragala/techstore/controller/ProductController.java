package com.fragala.techstore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fragala.techstore.dto.response.ProductResponse;
import com.fragala.techstore.dto.request.CreateProductRequest;
import com.fragala.techstore.dto.request.UpdateProductRequest;

import com.fragala.techstore.service.ProductService;

import jakarta.validation.Valid;


@RestController 
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping 
    public ResponseEntity<ProductResponse> create(
        @Valid @RequestBody CreateProductRequest request){
            
        ProductResponse response = productService.create(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
        
        }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        
        ProductResponse response = productService.update(id,request);    

        return ResponseEntity.ok(response);
            
        }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {

        ProductResponse response = productService.findById(id);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        List<ProductResponse> response = productService.findAll();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        productService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id){
        productService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}

