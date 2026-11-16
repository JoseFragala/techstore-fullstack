package com.fragala.techstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fragala.techstore.entity.Product;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.mapper.ProductMapper;
import com.fragala.techstore.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class PublicCatalogServiceTest {
    @Mock
    private ProductRepository products;
    @Mock
    private ProductMapper mapper;
    @InjectMocks
    private ProductService service;

    @Test
    void findAllActive_shouldUseActiveQuery_whenReadingPublicCatalog() {
        Product product = new Product();
        when(products.findAllByActiveTrue()).thenReturn(List.of(product));
        service.findAllActive();
        verify(mapper).toResponse(product);
        verify(products, never()).findAll();
    }

    @Test
    void findAllActive_shouldReturnEmptyList_whenNoActiveProductsExist() {
        when(products.findAllByActiveTrue()).thenReturn(List.of());
        assertTrue(service.findAllActive().isEmpty());
        verifyNoInteractions(mapper);
    }

    @Test
    void findActiveById_shouldMapProduct_whenActiveProductExists() {
        Product product = new Product();
        when(products.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(product));
        service.findActiveById(1L);
        verify(mapper).toResponse(product);
        verify(products, never()).findById(1L);
    }

    @Test
    void findActiveById_shouldThrowNotFound_whenProductIsMissingOrInactive() {
        when(products.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findActiveById(1L));
        verifyNoInteractions(mapper);
    }
}
