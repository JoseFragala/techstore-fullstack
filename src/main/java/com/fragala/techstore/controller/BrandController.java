package com.fragala.techstore.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fragala.techstore.dto.request.CreateBrandRequest;
import com.fragala.techstore.dto.request.UpdateBrandRequest;
import com.fragala.techstore.dto.response.BrandResponse;
import com.fragala.techstore.service.BrandService;

import java.util.List;

import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/brand")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService){
        this.brandService = brandService;

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BrandResponse create(@Valid @RequestBody CreateBrandRequest request){

        return brandService.create(request);
    }
    
    @PutMapping("/{id}")
    public BrandResponse update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateBrandRequest request){

        return brandService.update(id, request);
        }
    
    @GetMapping("/{id}")
    public BrandResponse findById(@PathVariable Long id){
        return brandService.findById(id);
    }

    @GetMapping
    public List<BrandResponse> findAll(){
        return brandService.findAll();
        
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        brandService.delete(id);
    }
}
