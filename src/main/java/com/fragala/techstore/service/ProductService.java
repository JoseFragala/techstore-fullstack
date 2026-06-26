package com.fragala.techstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fragala.techstore.entity.Product;
import com.fragala.techstore.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository repository; 

    public ProductService(ProductRepository repository){
        this.repository = repository;
    }

    public List<Product> getAllProducts(){
        return repository.findAll();
    }

    public Product getProductById(Long id) {
        return repository.findById(id)
            .orElseThrow();
    }
    
}
