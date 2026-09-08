package com.fragala.techstore.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fragala.techstore.dto.request.CreateProductImageRequest;
import com.fragala.techstore.dto.request.CreateProductRequest;
import com.fragala.techstore.dto.response.BrandResponse;
import com.fragala.techstore.dto.response.CategoryResponse;
import com.fragala.techstore.dto.response.ProductImageResponse;
import com.fragala.techstore.dto.response.ProductResponse;
import com.fragala.techstore.entity.Brand;
import com.fragala.techstore.entity.Category;
import com.fragala.techstore.entity.Product;
import com.fragala.techstore.entity.ProductImage;

@Component 
public class ProductMapper {

    public Product toEntity(
        CreateProductRequest request,
        Brand brand,
        Category category) {

           return new Product(
                request.getName(),
                request.getDescription(),
                brand,
                category,
                request.getPrice(),
                request.getSku(),
                request.getStock()
           );

        }
    
    public ProductResponse toResponse(Product product){
        
        BrandResponse brandResponse = new BrandResponse(
            product.getBrand().getId(),
            product.getBrand().getName()
        );

        CategoryResponse categoryResponse = new CategoryResponse(
            product.getCategory().getId(),
            product.getCategory().getName()
        );

        List<ProductImageResponse> images = product.getImages()
                .stream()
                .map(this::toImageResponse)
                .toList();

        return new ProductResponse(
              product.getId(),
                product.getName(),
                product.getDescription(),
                brandResponse,
                categoryResponse,
                product.getPrice(),
                product.getSku(),
                product.getStock(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                images
        );
    }

    public ProductImageResponse toImageResponse(ProductImage image){
        return new ProductImageResponse(
            image.getId(),
            image.getImageUrl(),
            image.getDisplayOrder()
        );
    }

    public ProductImage toImageEntity(CreateProductImageRequest request) {
        
        ProductImage image = new ProductImage();

        image.setImageUrl(request.getImageUrl());
        image.setDisplayOrder(request.getDisplayOrder());

        return image;
    }

    
}
