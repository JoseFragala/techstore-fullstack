package com.fragala.techstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fragala.techstore.dto.request.CreateCategoryRequest;
import com.fragala.techstore.dto.request.UpdateCategoryRequest;
import com.fragala.techstore.dto.response.CategoryResponse;
import com.fragala.techstore.entity.Category;
import com.fragala.techstore.exception.CategoryAlreadyExistsException;
import com.fragala.techstore.exception.ResourceNotFoundException;
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

    @Test // Category name Already Exist , should throw exception and never call the methods
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

    @Test // Category Name dont exist - can create the category.
    void create_shouldCreateCategory_whenNameDoesNotExist() {

        // GIVEN

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Laptops");

        when(categoryRepository.existsByName(request.getName()))
                .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //WHEN
        CategoryResponse response = categoryService.create(request);

        //THEN

        assertEquals("Laptops", response.getName());


        verify(categoryRepository).save(any(Category.class));

    }

    @Test // Successfully updated
    void update_shouldUpdateCategory_whenCategoryExistsAndNameIsAvailable(){

        Long id = 1L;

        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("Laptops");

        Category category = new Category();
        category.setName("Computers");

        when(categoryRepository.existsByNameAndIdNot(request.getName(), id))
            .thenReturn(false);

        when(categoryRepository.findById(id))
            .thenReturn(Optional.of(category));
        
        when(categoryRepository.save(any(Category.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponse response = categoryService.update(id, request);

        assertEquals("Laptops", response.getName());

        verify(categoryRepository).save(category);
        
    }
    @Test // Id not found
    void update_shouldThrowException_whenCategoryDoesNotExist(){

        Long id = 1L;

        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("Laptops");

        when(categoryRepository.existsByNameAndIdNot(request.getName(), id))
            .thenReturn(false);
        
        when(categoryRepository.findById(id))
            .thenReturn(Optional.empty());
        
        assertThrows(
            ResourceNotFoundException.class,
            () -> categoryService.update(id, request)

        );

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test // the name already belongs to other category.
    void update_shouldThrowException_whenNameAlreadyExists() {

        Long id = 1L;

        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("Laptops");

        when(categoryRepository.existsByNameAndIdNot(request.getName(), id))
                .thenReturn(true);
        
        assertThrows(
            CategoryAlreadyExistsException.class,
            () -> categoryService.update(id, request)
        );

        verify(categoryRepository, never()).findById(id);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test // Category found
    void findById_shouldReturnCategory_whenCategoryExists(){

         Long id = 1L;

            Category category = new Category();
            category.setName("Laptops");

            when(categoryRepository.findById(id))
                    .thenReturn(Optional.of(category));

            CategoryResponse response = categoryService.findById(id);

            assertEquals("Laptops", response.getName());

            verify(categoryRepository).findById(id);
    }

    @Test // CategoryNotfound 
    void findById_shouldThrowException_whenCategoryDoesNotExist() {

        Long id = 1L;

            when(categoryRepository.findById(id))
            .thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> categoryService.findById(id)
             );
        }

        @Test // Category found
            void findAll_shouldReturnCategories() {

                Category category1 = new Category();
                category1.setName("Laptops");

                Category category2 = new Category();
                category2.setName("Smartphones");

                when(categoryRepository.findAll())
                        .thenReturn(List.of(category1, category2));

                List<CategoryResponse> response = categoryService.findAll();

                assertEquals(2, response.size());
                assertEquals("Laptops", response.get(0).getName());
                assertEquals("Smartphones", response.get(1).getName());

                verify(categoryRepository).findAll();
            }


        @Test // empty list
        void findAll_shouldReturnEmptyList_whenNoCategoriesExist() {

            when(categoryRepository.findAll())
                    .thenReturn(List.of());

            List<CategoryResponse> response = categoryService.findAll();

            assertEquals(0, response.size());

            verify(categoryRepository).findAll();
        }

        @Test // category found
        void delete_shouldDeleteCategory_whenCategoryExists() {

            Long id = 1L;

            Category category = new Category();
            category.setName("Laptops");

            when(categoryRepository.findById(id))
                    .thenReturn(Optional.of(category));

            categoryService.delete(id);

            verify(categoryRepository).delete(category);
        }

        @Test// category not found
        void delete_shouldThrowException_whenCategoryDoesNotExist() {

            Long id = 1L;

            when(categoryRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> categoryService.delete(id)
            );

            verify(categoryRepository, never()).delete(any(Category.class));
        }
        

}
