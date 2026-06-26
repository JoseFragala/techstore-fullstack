package com.fragala.techstore.controller;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fragala.techstore.entity.Product;
import com.fragala.techstore.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController (ProductService service){
        this.service = service;
    }
 
    @GetMapping
    public List<Product> getProducts(){
        return service.getAllProducts();
    }
    
    @GetMapping("/{id}")
    public Product getProduct(
        @PathVariable Long id){

            return service.getProductById(id);
        }
    
}
