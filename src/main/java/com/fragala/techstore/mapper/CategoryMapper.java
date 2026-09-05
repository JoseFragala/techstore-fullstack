package com.fragala.techstore.mapper;

import org.springframework.stereotype.Component;

import com.fragala.techstore.dto.request.CreateCategoryRequest;
import com.fragala.techstore.dto.response.CategoryResponse;
import com.fragala.techstore.entity.Category;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category){
            return new CategoryResponse(
                category.getId(),
                category.getName()

            );
        }

    public Category toEntity(CreateCategoryRequest request){
        Category category = new Category();
            category.setName(request.getName());
        return category;

    }


    
}
