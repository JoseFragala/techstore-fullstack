package com.fragala.techstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fragala.techstore.dto.request.CreateBrandRequest;
import com.fragala.techstore.dto.request.UpdateBrandRequest;
import com.fragala.techstore.dto.response.BrandResponse;
import com.fragala.techstore.entity.Brand;
import com.fragala.techstore.exception.BrandAlreadyExistsException;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.mapper.BrandMapper;
import com.fragala.techstore.repository.BrandRepository;

@Service
public class BrandService {
    
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public BrandService(BrandRepository brandRepository, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    public BrandResponse create(CreateBrandRequest request){

        if(brandRepository.existsByName(request.getName())){
            throw new BrandAlreadyExistsException("This brand already exists");
        }
        Brand brand = new Brand();
        brand.setName(request.getName());

        Brand savedBrand = brandRepository.save(brand);

        return brandMapper.toResponse(savedBrand);
        
    }

    public BrandResponse update(Long id, UpdateBrandRequest request){

        if(brandRepository.existsByNameAndIdNot(request.getName(), id)){
            throw new BrandAlreadyExistsException("This brand already exists");
        }
        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        brand.setName(request.getName());

        Brand savedBrand = brandRepository.save(brand);

        return brandMapper.toResponse(savedBrand);
    }
    
    public BrandResponse findById(Long id) {

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
        
        return brandMapper.toResponse(brand);
    }

    public List<BrandResponse> findAll() {

        return brandRepository.findAll()
            .stream()
            .map(brandMapper::toResponse)
            .toList();
    }

    public void delete(Long id){

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
        
        brandRepository.delete(brand);

    }
            
    }

