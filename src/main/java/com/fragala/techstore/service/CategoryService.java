package com.fragala.techstore.service;

import org.springframework.stereotype.Service;

import com.fragala.techstore.dto.request.CreateCategoryRequest;
import com.fragala.techstore.dto.response.CategoryResponse;
import com.fragala.techstore.entity.Category;
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
    
    // Repositories are injected into services because services own business use cases,
    // while repositories focus only on persistence operations.
    private final CategoryRepository categoryRepository;

    /**
     * Creates the service with the repository dependency it needs.
     *
     * <p>Constructor injection is preferred because it makes dependencies explicit, supports
     * immutability with {@code final} fields, and makes the class easier to test.
     *
     * @param categoryRepository repository used to persist and retrieve categories
     */
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new category from the incoming request data.
     *
     * <p>Business responsibility: translate request data into a domain entity, persist it, and
     * return a response DTO that exposes only the information relevant to the client.
     *
     * @param request request DTO carrying the category data sent by the client
     * @return response DTO containing the persisted category information
     */
    public CategoryResponse create(CreateCategoryRequest request){
        // A fresh entity instance is created so the service controls which incoming fields
        // are copied into the persistence model.
        Category category = new Category();
        category.setName(request.getName());

        // Persisting through the repository delegates the SQL generation and entity state
        // management to Spring Data JPA and Hibernate.
        Category savedCategory = categoryRepository.save(category);

        // Returning a response DTO avoids exposing the entity directly and keeps the API
        // contract independent from internal persistence details.
        return new CategoryResponse(
            savedCategory.getId(),
            savedCategory.getName()
        );
        
    }
            
    }

