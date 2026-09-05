package com.fragala.techstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fragala.techstore.dto.request.CreateCategoryRequest;
import com.fragala.techstore.dto.request.UpdateCategoryRequest;
import com.fragala.techstore.dto.response.CategoryResponse;
import com.fragala.techstore.entity.Category;
import com.fragala.techstore.exception.CategoryAlreadyExistsException;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.repository.CategoryRepository;

/**
 * Service responsible for category-related business operations.
 *
 * <p>This class represents the business layer for category use cases. It exists so business rules
 * stay separate from both HTTP handling and direct database access. In Spring applications,
 * controllers should remain thin and delegate workflow decisions to services like this one.
 *
 * <p>Architecturally, this class sits between the controller layer and the repository layer. It
 * receives input data, coordinates persistence work, and prepares response objects.
 *
 * <p>This service is used whenever the application needs to create or manage categories according
 * to business requirements.
 */
// `@Service` marks this class as a Spring-managed service component. It is used here to
// indicate that this class contains business logic and should be discovered for dependency
// injection by Spring's component scan.
@Service
public class CategoryService {
    
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse create(CreateCategoryRequest request){

        if(categoryRepository.existsByName(request.getName())){
            throw new CategoryAlreadyExistsException("This category already exists");
        }
        Category category = new Category();
        category.setName(request.getName());

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(
            savedCategory.getId(),
            savedCategory.getName()
        );
        
    }

    public CategoryResponse update(Long id, UpdateCategoryRequest request){

        if(categoryRepository.existsByNameAndIdNot(request.getName(), id)){
            throw new CategoryAlreadyExistsException("This category already exists");
        }
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setName(request.getName());
        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(
            savedCategory.getId(),
            savedCategory.getName()
        );
    }
    
    public CategoryResponse findById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        return new CategoryResponse(
            category.getId(),
            category.getName()
        );
    }

    public List<CategoryResponse> findAll() {

        return categoryRepository.findAll()
            .stream()
            .map(category -> new CategoryResponse(
                category.getId(),
                category.getName()

            ))
            .toList();
    }

    public void delete(Long id){

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        categoryRepository.delete(category);

    }
            
    }

