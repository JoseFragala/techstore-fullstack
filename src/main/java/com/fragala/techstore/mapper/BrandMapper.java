package com.fragala.techstore.mapper;

import org.springframework.stereotype.Component;

import com.fragala.techstore.dto.request.CreateBrandRequest;
import com.fragala.techstore.dto.response.BrandResponse;
import com.fragala.techstore.entity.Brand;

@Component 
public class BrandMapper {

    public BrandResponse toResponse(Brand brand){
            return new BrandResponse(
                brand.getId(),
                brand.getName()

            );
        }

    public Brand toEntity(CreateBrandRequest request){
        Brand brand = new Brand();
            brand.setName(request.getName());
        return brand;

    }


    
}
