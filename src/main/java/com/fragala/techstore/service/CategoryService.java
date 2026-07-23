package com.fragala.techstore.service;

import org.springframework.stereotype.Service;

import com.fragala.techstore.dto.request.CreateCategoryRequest;
import com.fragala.techstore.dto.response.CategoryResponse;
import com.fragala.techstore.entity.Category;
import com.fragala.techstore.repository.CategoryRepository;

@Service
public class CategoryService {
    

    private final CategoryRepository categoryRepository;
// constructor
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

// methods
    
    public CategoryResponse create(CreateCategoryRequest request){
        Category category = new Category();
        category.setName(request.getName());

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(
            savedCategory.getId(),
            savedCategory.getName()
        );
        
    }
            
    }

