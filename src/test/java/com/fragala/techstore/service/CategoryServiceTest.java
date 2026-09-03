package com.fragala.techstore.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fragala.techstore.dto.request.CreateCategoryRequest;
import com.fragala.techstore.entity.Category;
import com.fragala.techstore.exception.CategoryAlreadyExistsException;
import com.fragala.techstore.repository.CategoryRepository;

public class CategoryServiceTest {
    
    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;

    @BeforeEach()
    void setUp(){
        MockitoAnnotations.openMocks(this);

        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    void create_shouldThrowException_whenCategoryNameAlreadyExists(){

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Laptops");

        when(categoryRepository.existsByName(request.getName()))
                .thenReturn(true);

        assertThrows(
            CategoryAlreadyExistsException.class,
            () -> categoryService.create(request)
        );
        verify(categoryRepository, never()).save(any(Category.class));
    }
}
