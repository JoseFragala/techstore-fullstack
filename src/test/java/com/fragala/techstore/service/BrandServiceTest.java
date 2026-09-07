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

import com.fragala.techstore.dto.request.CreateBrandRequest;
import com.fragala.techstore.dto.request.UpdateBrandRequest;
import com.fragala.techstore.dto.response.BrandResponse;
import com.fragala.techstore.entity.Brand;
import com.fragala.techstore.exception.BrandAlreadyExistsException;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.mapper.BrandMapper;
import com.fragala.techstore.repository.BrandRepository;

public class BrandServiceTest {
    
    @Mock
    private BrandRepository brandRepository;

    @Mock
    private BrandMapper brandMapper;

    private BrandService brandService;



    @BeforeEach()
    void setUp(){
        MockitoAnnotations.openMocks(this);

        brandService = new BrandService(brandRepository, brandMapper);
    }

    @Test // Brand name Already Exist , should throw exception and never call the methods
    void create_shouldThrowException_whenBrandNameAlreadyExists(){

        CreateBrandRequest request = new CreateBrandRequest();
        request.setName("Samsung");

        when(brandRepository.existsByName(request.getName()))
                .thenReturn(true);

        assertThrows(
            BrandAlreadyExistsException.class,
            () -> brandService.create(request)
        );
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test // Brand Name dont exist - can create the brand.
    void create_shouldCreateBrand_whenNameDoesNotExist() {

    // GIVEN

    CreateBrandRequest request = new CreateBrandRequest();
    request.setName("Samsung");

    when(brandRepository.existsByName(request.getName()))
            .thenReturn(false);

    when(brandRepository.save(any(Brand.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    BrandResponse expectedResponse =
            new BrandResponse(1L, "Samsung");

    when(brandMapper.toResponse(any(Brand.class)))
            .thenReturn(expectedResponse);

    // WHEN

    BrandResponse response = brandService.create(request);

    // THEN

    assertEquals("Samsung", response.getName());

    verify(brandRepository).save(any(Brand.class));
}

    @Test // Successfully updated
    void update_shouldUpdateBrand_whenBrandExistsAndNameIsAvailable() {

        Long id = 1L;

        UpdateBrandRequest request = new UpdateBrandRequest();
        request.setName("Samsung");

        Brand brand = new Brand();
        brand.setName("Computers");

        when(brandRepository.existsByNameAndIdNot(request.getName(), id))
                .thenReturn(false);

        when(brandRepository.findById(id))
                .thenReturn(Optional.of(brand));

        when(brandRepository.save(any(Brand.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BrandResponse expectedResponse =
                new BrandResponse(1L, "Samsung");

        when(brandMapper.toResponse(any(Brand.class)))
                .thenReturn(expectedResponse);

        // WHEN

        BrandResponse response = brandService.update(id, request);

        // THEN

        assertEquals("Samsung", response.getName());

        verify(brandRepository).save(brand);
    }
    @Test // Id not found
    void update_shouldThrowException_whenBrandDoesNotExist(){

        Long id = 1L;

        UpdateBrandRequest request = new UpdateBrandRequest();
        request.setName("Samsung");

        when(brandRepository.existsByNameAndIdNot(request.getName(), id))
            .thenReturn(false);
        
        when(brandRepository.findById(id))
            .thenReturn(Optional.empty());
        
        assertThrows(
            ResourceNotFoundException.class,
            () -> brandService.update(id, request)

        );

        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test // the name already belongs to other brand.
    void update_shouldThrowException_whenNameAlreadyExists() {

        Long id = 1L;

        UpdateBrandRequest request = new UpdateBrandRequest();
        request.setName("Samsung");

        when(brandRepository.existsByNameAndIdNot(request.getName(), id))
                .thenReturn(true);
        
        assertThrows(
            BrandAlreadyExistsException.class,
            () -> brandService.update(id, request)
        );

        verify(brandRepository, never()).findById(id);
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test // Brand found
    void findById_shouldReturnBrand_whenBrandExists() {

            Long id = 1L;

            Brand brand = new Brand();
            brand.setName("Samsung");

            when(brandRepository.findById(id))
                    .thenReturn(Optional.of(brand));

            BrandResponse expectedResponse =
                    new BrandResponse(1L, "Samsung");

            when(brandMapper.toResponse(brand))
                    .thenReturn(expectedResponse);

            // WHEN

            BrandResponse response = brandService.findById(id);

            // THEN

            assertEquals("Samsung", response.getName());

            verify(brandRepository).findById(id);
        }

    @Test // BrandNotfound 
    void findById_shouldThrowException_whenBrandDoesNotExist() {

        Long id = 1L;

            when(brandRepository.findById(id))
            .thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> brandService.findById(id)
             );
        }

        @Test // Brand found
            void findAll_shouldReturnBrand() {

                Brand brand1 = new Brand();
                brand1.setName("Samsung");

                Brand brand2 = new Brand();
                brand2.setName("Apple");

                when(brandRepository.findAll())
                        .thenReturn(List.of(brand1, brand2));

                BrandResponse response1 =
                        new BrandResponse(1L, "Samsung");

                BrandResponse response2 =
                        new BrandResponse(2L, "Apple");

                when(brandMapper.toResponse(brand1))
                        .thenReturn(response1);

                when(brandMapper.toResponse(brand2))
                        .thenReturn(response2);

                // WHEN

                List<BrandResponse> response = brandService.findAll();

                // THEN

                assertEquals(2, response.size());
                assertEquals("Samsung", response.get(0).getName());
                assertEquals("Apple", response.get(1).getName());

                verify(brandRepository).findAll();
            }

        @Test // empty list
        void findAll_shouldReturnEmptyList_whenNoBrandExist() {

            when(brandRepository.findAll())
                    .thenReturn(List.of());

            List<BrandResponse> response = brandService.findAll();

            assertEquals(0, response.size());

            verify(brandRepository).findAll();
        }

        @Test // brand found
        void delete_shouldDeleteBrand_whenBrandExists() {

            Long id = 1L;

            Brand brand = new Brand();
            brand.setName("Samsung");

            when(brandRepository.findById(id))
                    .thenReturn(Optional.of(brand));

            brandService.delete(id);

            verify(brandRepository).delete(brand);
        }

        @Test// brand not found
        void delete_shouldThrowException_whenBrandDoesNotExist() {

            Long id = 1L;

            when(brandRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> brandService.delete(id)
            );

            verify(brandRepository, never()).delete(any(Brand.class));
        }
        

}
