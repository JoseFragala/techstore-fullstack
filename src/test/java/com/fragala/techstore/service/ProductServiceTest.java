package com.fragala.techstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fragala.techstore.dto.request.CreateProductRequest;
import com.fragala.techstore.dto.request.CreateProductImageRequest;
import com.fragala.techstore.dto.request.UpdateProductRequest;
import com.fragala.techstore.dto.response.BrandResponse;
import com.fragala.techstore.dto.response.CategoryResponse;
import com.fragala.techstore.dto.response.ProductResponse;
import com.fragala.techstore.entity.Brand;
import com.fragala.techstore.entity.Category;
import com.fragala.techstore.entity.Product;
import com.fragala.techstore.entity.ProductImage;
import com.fragala.techstore.exception.ProductAlreadyExistsException;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.mapper.ProductMapper;
import com.fragala.techstore.repository.BrandRepository;
import com.fragala.techstore.repository.CategoryRepository;
import com.fragala.techstore.repository.ProductRepository;
import com.fragala.techstore.repository.CartItemRepository;
import com.fragala.techstore.repository.OrderItemRepository;




@ExtendWith (MockitoExtension.class)
public class ProductServiceTest {
    

    @Mock 
    private ProductRepository productRepository;

    @Mock 
    private ProductMapper productMapper;

    @Mock 
    private BrandRepository brandRepository;

    @Mock 
    private CategoryRepository categoryRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks 
    private ProductService productService;


    @Test // Create - valid request
    void create_shouldCreateProduct_whenRequestIsValid() {

        // GIVEN
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Gaming Mouse");
        request.setDescription("Wireless gaming mouse");
        request.setBrandId(1L);
        request.setCategoryId(2L);
        request.setPrice(new BigDecimal("299.90"));
        request.setSku("MOUSE-001");
        request.setStock(10);
        request.setImages(List.of());

        Brand brand = new Brand();
        brand.setName("Logitech");

        Category category = new Category();
        category.setName("Mice");

        Product product = new Product(
            request.getName(),
            request.getDescription(),
            brand,
            category,
            request.getPrice(),
            request.getSku(),
            request.getStock()
        );

        ProductResponse response = new ProductResponse(
            null,
            product.getName(),
            product.getDescription(),
            null,
            null,
            product.getPrice(),
            product.getSku(),
            product.getStock(),
            product.isActive(),
            null,
            null,
            List.of()
        );

        when(brandRepository.findById(1L))
            .thenReturn(java.util.Optional.of(brand));

        when(categoryRepository.findById(2L))
            .thenReturn(java.util.Optional.of(category));
        
        when(productRepository.existsBySku("MOUSE-001"))
            .thenReturn(false);
        
        when(productMapper.toEntity(request, brand, category))
            .thenReturn(product);

        when(productRepository.save(product))
            .thenReturn(product);
        
        when(productMapper.toResponse(product))
            .thenReturn(response);
        
        //WHEN
        ProductResponse result = productService.create(request);
        
        //THEN
        assertEquals("Gaming Mouse", result.getName());
        assertEquals("MOUSE-001", result.getSku());
        assertEquals(new BigDecimal("299.90"), result.getPrice());
        
    }

    @Test // Brand not found
    void create_shouldThrowException_whenBrandDoesNotExist(){

        //GIVEN
        CreateProductRequest request = new CreateProductRequest();
        request.setBrandId(999L);
        request.setCategoryId(2L);
        request.setSku("MOUSE-001");

        when(brandRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        //WHEN + THEN

        assertThrows(
            ResourceNotFoundException.class,
             () -> productService.create(request)
         );
    }
     @Test // Category not found
    void create_shouldThrowException_whenCategoryDoesNotExist(){

        //GIVEN
        CreateProductRequest request = new CreateProductRequest();
        request.setBrandId(1L);
        request.setCategoryId(999L);
        request.setSku("MOUSE-001");

        Brand brand = new Brand();

        when(brandRepository.findById(1L))
                .thenReturn(java.util.Optional.of(brand));
        when(categoryRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        //WHEN + THEN

        assertThrows(
            ResourceNotFoundException.class,
             () -> productService.create(request)
         );
    }

    @Test // Sku already existis
    void create_shouldThrowException_whenSkuAlreadyExists(){
        //GIVEN
        CreateProductRequest request = new CreateProductRequest();
        request.setBrandId(1L);
        request.setCategoryId(2L);
        request.setSku("MOUSE-001");

        Brand brand = new Brand();

        Category category = new Category();

        when(brandRepository.findById(1L))
            .thenReturn(java.util.Optional.of(brand));
        
        when(categoryRepository.findById(2L))
            .thenReturn(java.util.Optional.of(category));
        
        when(productRepository.existsBySku("MOUSE-001"))
            .thenReturn(true);
        
        
        //WHEN + THEN
        assertThrows(
            ProductAlreadyExistsException.class,
            () -> productService.create(request)
        );
        
    }
    @Test // rule maximum 4 images.
    void create_shouldThrowException_whenProductHasMoreThanFourImages(){

        //GIVEN
        CreateProductRequest request = new CreateProductRequest();
        request.setBrandId(1L);
        request.setCategoryId(2L);
        request.setSku("MOUSE-001");

        request.setImages(List.of(
            new CreateProductImageRequest(),
            new CreateProductImageRequest(),
            new CreateProductImageRequest(),
            new CreateProductImageRequest(),
            new CreateProductImageRequest()
        ));

    Brand brand = new Brand();
    Category category = new Category();

    when(brandRepository.findById(1L))
            .thenReturn(java.util.Optional.of(brand));

    when(categoryRepository.findById(2L))
            .thenReturn(java.util.Optional.of(category));

    when(productRepository.existsBySku("MOUSE-001"))
            .thenReturn(false);

    // WHEN + THEN
    assertThrows(
            IllegalArgumentException.class,
            () -> productService.create(request)
    );

    }
    @Test // with images valid
    void create_shouldCreateProductWithImages_whenRequestIsValid() {

        // GIVEN
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Gaming Mouse");
        request.setDescription("Wireless gaming mouse");
        request.setBrandId(1L);
        request.setCategoryId(2L);
        request.setPrice(new BigDecimal("299.90"));
        request.setSku("MOUSE-002");
        request.setStock(10);

        CreateProductImageRequest imageRequest = new CreateProductImageRequest();
        imageRequest.setImageUrl("https://example.com/mouse.jpg");
        imageRequest.setDisplayOrder(1);

        request.setImages(List.of(imageRequest));

        Brand brand = new Brand();
        Category category = new Category();

        Product product = new Product(
                request.getName(),
                request.getDescription(),
                brand,
                category,
                request.getPrice(),
                request.getSku(),
                request.getStock()
        );

        ProductImage image = new ProductImage();

        ProductResponse response = new ProductResponse(
                null,
                product.getName(),
                product.getDescription(),
                null,
                null,
                product.getPrice(),
                product.getSku(),
                product.getStock(),
                product.isActive(),
                null,
                null,
                List.of()
        );

        when(brandRepository.findById(1L))
                .thenReturn(java.util.Optional.of(brand));

        when(categoryRepository.findById(2L))
                .thenReturn(java.util.Optional.of(category));

        when(productRepository.existsBySku("MOUSE-002"))
                .thenReturn(false);

        when(productMapper.toEntity(request, brand, category))
                .thenReturn(product);

        when(productMapper.toImageEntity(imageRequest))
                .thenReturn(image);

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(response);

        // WHEN
        ProductResponse result = productService.create(request);

        // THEN
        assertEquals("MOUSE-002", result.getSku());
    }

    @Test // Update valid request.
    void update_shouldUpdateProduct_whenRequestIsValid() {

            // GIVEN
            Long productId = 1L;

            UpdateProductRequest request = new UpdateProductRequest();
            request.setName("Updated Gaming Mouse");
            request.setDescription("Updated wireless gaming mouse");
            request.setBrandId(2L);
            request.setCategoryId(3L);
            request.setPrice(new BigDecimal("349.90"));
            request.setStock(15);

            Brand oldBrand = new Brand();
            oldBrand.setName("Logitech");

            Category oldCategory = new Category();
            oldCategory.setName("Mice");

            Product product = new Product(
                    "Gaming Mouse",
                    "Wireless gaming mouse",
                    oldBrand,
                    oldCategory,
                    new BigDecimal("299.90"),
                    "MOUSE-001",
                    10
            );

            Brand newBrand = new Brand();
            newBrand.setName("Razer");

            Category newCategory = new Category();
            newCategory.setName("Gaming Mice");

            ProductResponse response = new ProductResponse(
                    productId,
                    "Updated Gaming Mouse",
                    "Updated wireless gaming mouse",
                    null,
                    null,
                    new BigDecimal("349.90"),
                    "MOUSE-001",
                    15,
                    true,
                    null,
                    null,
                    List.of()
            );

            when(productRepository.findById(productId))
                    .thenReturn(java.util.Optional.of(product));

            when(brandRepository.findById(2L))
                    .thenReturn(java.util.Optional.of(newBrand));

            when(categoryRepository.findById(3L))
                    .thenReturn(java.util.Optional.of(newCategory));

            when(productRepository.save(product))
                    .thenReturn(product);

            when(productMapper.toResponse(product))
                    .thenReturn(response);

            // WHEN
            ProductResponse result = productService.update(productId, request);

            // THEN
            assertEquals("Updated Gaming Mouse", result.getName());
            assertEquals("Updated wireless gaming mouse", result.getDescription());
            assertEquals(new BigDecimal("349.90"), result.getPrice());
            assertEquals(15, result.getStock());
            assertEquals("MOUSE-001", result.getSku());
        }

        @Test // update brand not found
        void update_shouldThrowException_whenProductDoesNotExist() {

                // GIVEN
                Long productId = 999L;

                UpdateProductRequest request = new UpdateProductRequest();
                request.setName("Updated Gaming Mouse");

                when(productRepository.findById(productId))
                        .thenReturn(java.util.Optional.empty());

                // WHEN + THEN
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> productService.update(productId, request)
                );
            }

            @Test // update Category not found
            void update_shouldThrowException_whenCategoryDoesNotExist() {

                    // GIVEN
                    Long productId = 1L;

                    UpdateProductRequest request = new UpdateProductRequest();
                    request.setBrandId(2L);
                    request.setCategoryId(999L);

                    Brand brand = new Brand();

                    Product product = new Product(
                            "Gaming Mouse",
                            "Wireless gaming mouse",
                            brand,
                            new Category(),
                            new BigDecimal("299.90"),
                            "MOUSE-001",
                            10
                    );

                    when(productRepository.findById(productId))
                            .thenReturn(java.util.Optional.of(product));

                    when(brandRepository.findById(2L))
                            .thenReturn(java.util.Optional.of(brand));

                    when(categoryRepository.findById(999L))
                            .thenReturn(java.util.Optional.empty());

                    // WHEN + THEN
                    assertThrows(
                            ResourceNotFoundException.class,
                            () -> productService.update(productId, request)
                    );
                }

                @Test // Unchanged sKu
                void update_shouldKeepSkuUnchanged() {

                        // GIVEN
                        Long productId = 1L;

                        Brand brand = new Brand();
                        Category category = new Category();

                        Product product = new Product(
                                "Gaming Mouse",
                                "Wireless gaming mouse",
                                brand,
                                category,
                                new BigDecimal("299.90"),
                                "MOUSE-001",
                                10
                        );

                        UpdateProductRequest request = new UpdateProductRequest();
                        request.setName("Updated Gaming Mouse");
                        request.setDescription("Updated description");
                        request.setBrandId(2L);
                        request.setCategoryId(3L);
                        request.setPrice(new BigDecimal("349.90"));
                        request.setStock(20);

                        Brand newBrand = new Brand();
                        Category newCategory = new Category();

                        when(productRepository.findById(productId))
                                .thenReturn(java.util.Optional.of(product));

                        when(brandRepository.findById(2L))
                                .thenReturn(java.util.Optional.of(newBrand));

                        when(categoryRepository.findById(3L))
                                .thenReturn(java.util.Optional.of(newCategory));

                        when(productRepository.save(product))
                                .thenReturn(product);

                        ProductResponse response = new ProductResponse(
                                productId,
                                "Updated Gaming Mouse",
                                "Updated description",
                                null,
                                null,
                                new BigDecimal("349.90"),
                                "MOUSE-001",
                                20,
                                true,
                                null,
                                null,
                                List.of()
                        );

                        when(productMapper.toResponse(product))
                                .thenReturn(response);

                        // WHEN
                        ProductResponse result = productService.update(productId, request);

                        // THEN
                        assertEquals("MOUSE-001", result.getSku());
                    }

                @Test // findbyid product exist.
                void findById_shouldReturnProduct_whenProductExists() {

                    Long productId = 1L;

                    Brand brand = new Brand();
                    Category category = new Category();
                    Product product = new Product(
                            "Laptop",
                            "Gaming laptop",
                            brand,
                            category,
                            new BigDecimal("4999.90"),
                            "LAP-001",
                            10
                    );

                    ProductResponse response = new ProductResponse(
                            productId,
                            "Laptop",
                            "Gaming laptop",
                            new BrandResponse(1L, "Brand"),
                            new CategoryResponse(1L, "Category"),
                            new BigDecimal("4999.90"),
                            "LAP-001",
                            10,
                            true,
                            null,
                            null,
                            List.of()
                    );

                    when(productRepository.findById(productId))
                            .thenReturn(Optional.of(product));

                    when(productMapper.toResponse(product))
                            .thenReturn(response);

                    ProductResponse result = productService.findById(productId);

                    assertEquals(productId, result.getId());
                    assertEquals("Laptop", result.getName());
                    assertEquals("LAP-001", result.getSku());

                    verify(productRepository).findById(productId);
                    verify(productMapper).toResponse(product);
                }    

                @Test // findbyid productnotfound
                void findById_shouldThrowException_whenProductDoesNotExist() {

                    Long productId = 1L;

                    when(productRepository.findById(productId))
                            .thenReturn(Optional.empty());

                    assertThrows(
                            ResourceNotFoundException.class,
                            () -> productService.findById(productId)
                    );

                    verify(productRepository).findById(productId);
                    verifyNoInteractions(productMapper);
                }

                @Test // find all product exists.
                void findAll_shouldReturnProducts_whenProductsExist() {

                    Product product1 = new Product(
                            "Laptop",
                            "Gaming laptop",
                            new Brand(),
                            new Category(),
                            new BigDecimal("4999.90"),
                            "LAP-001",
                            10
                    );

                    Product product2 = new Product(
                            "Keyboard",
                            "Mechanical keyboard",
                            new Brand(),
                            new Category(),
                            new BigDecimal("399.90"),
                            "KEY-001",
                            20
                    );

                    ProductResponse response1 = new ProductResponse(
                            1L,
                            "Laptop",
                            "Gaming laptop",
                            new BrandResponse(1L, "Brand"),
                            new CategoryResponse(1L, "Category"),
                            new BigDecimal("4999.90"),
                            "LAP-001",
                            10,
                            true,
                            null,
                            null,
                            List.of()
                    );

                    ProductResponse response2 = new ProductResponse(
                            2L,
                            "Keyboard",
                            "Mechanical keyboard",
                            new BrandResponse(2L, "Brand"),
                            new CategoryResponse(2L, "Category"),
                            new BigDecimal("399.90"),
                            "KEY-001",
                            20,
                            true,
                            null,
                            null,
                            List.of()
                    );

                    when(productRepository.findAll())
                            .thenReturn(List.of(product1, product2));

                    when(productMapper.toResponse(product1))
                            .thenReturn(response1);

                    when(productMapper.toResponse(product2))
                            .thenReturn(response2);

                    List<ProductResponse> result = productService.findAll();

                    assertEquals(2, result.size());
                    assertEquals("Laptop", result.get(0).getName());
                    assertEquals("Keyboard", result.get(1).getName());

                    verify(productRepository).findAll();
                    verify(productMapper).toResponse(product1);
                    verify(productMapper).toResponse(product2);
                }

                @Test //  findall product not found
                void findAll_shouldReturnEmptyList_whenNoProductsExist() {

                    when(productRepository.findAll())
                            .thenReturn(List.of());

                    List<ProductResponse> result = productService.findAll();

                    assertTrue(result.isEmpty());

                    verify(productRepository).findAll();
                    verifyNoInteractions(productMapper);
                }
                @Test // deactivate product exists.
                void deactivate_shouldSetProductInactive_whenProductExists() {
                    Long productId = 1L;

                    Product product = new Product(
                        "Laptop",
                        "Gaming laptop",
                        new Brand(),
                        new Category(),
                        new BigDecimal("4999.90"),
                        "LAP-001",
                        10
                    );

                    when(productRepository.findById(productId))
                            .thenReturn(Optional.of(product));

                    assertTrue(product.isActive());

                    productService.deactivate(productId);

                    assertFalse(product.isActive());

                    verify(productRepository).findById(productId);
                    verify(productRepository).save(product);
                }

                @Test // product not found
                void deactivate_shouldThrowException_whenProductDoesNotExist() {
                    Long productId = 1L;

                    when(productRepository.findById(productId))
                            .thenReturn(Optional.empty());

                    assertThrows(
                        ResourceNotFoundException.class,
                        () -> productService.deactivate(productId)
                    );

                    verify(productRepository).findById(productId);
                    verify(productRepository, never()).save(any(Product.class));
                }

                @Test // activate product exists
                void activate_shouldSetProductActive_whenProductExists() {
                    Long productId = 1L;

                    Product product = new Product(
                        "Laptop",
                        "Gaming laptop",
                        new Brand(),
                        new Category(),
                        new BigDecimal("4999.90"),
                        "LAP-001",
                        10
                    );

                    product.deactivate();

                    when(productRepository.findById(productId))
                            .thenReturn(Optional.of(product));

                    assertFalse(product.isActive());

                    productService.activate(productId);

                    assertTrue(product.isActive());

                    verify(productRepository).findById(productId);
                    verify(productRepository).save(product);
                }

                @Test // not found
                void activate_shouldThrowException_whenProductDoesNotExist() {
                    Long productId = 1L;

                    when(productRepository.findById(productId))
                            .thenReturn(Optional.empty());

                    assertThrows(
                        ResourceNotFoundException.class,
                        () -> productService.activate(productId)
                    );

                    verify(productRepository).findById(productId);
                    verify(productRepository, never()).save(any(Product.class));
                }

                @Test // harddelete -without references
                void delete_shouldHardDeleteProduct_whenProductHasNoReferences() {
                    Long productId = 1L;

                    Product product = new Product(
                        "Laptop",
                        "Gaming laptop",
                        new Brand(),
                        new Category(),
                        new BigDecimal("4999.90"),
                        "LAP-001",
                        10
                    );

                    when(productRepository.findById(productId))
                            .thenReturn(Optional.of(product));

                    when(cartItemRepository.existsByProductId(productId))
                            .thenReturn(false);

                    when(orderItemRepository.existsByProductId(productId))
                            .thenReturn(false);

                    productService.delete(productId);

                    verify(productRepository).findById(productId);
                    verify(cartItemRepository).existsByProductId(productId);
                    verify(orderItemRepository).existsByProductId(productId);
                    verify(productRepository).delete(product);

                    verify(productRepository, never()).save(any(Product.class));
                }
                @Test // product not found
                void delete_shouldThrowException_whenProductDoesNotExist() {
                    Long productId = 1L;

                    when(productRepository.findById(productId))
                            .thenReturn(Optional.empty());

                    assertThrows(
                        ResourceNotFoundException.class,
                        () -> productService.delete(productId)
                    );

                    verify(productRepository).findById(productId);

                    verifyNoInteractions(
                        cartItemRepository,
                        orderItemRepository
                    );

                    verify(productRepository, never()).delete(any(Product.class));
                }

                @Test // exist on cartItem need to deactivate.
                void delete_shouldDeactivateProduct_whenProductHasCartItems() {
                    Long productId = 1L;

                    Product product = new Product(
                        "Laptop",
                        "Gaming laptop",
                        new Brand(),
                        new Category(),
                        new BigDecimal("4999.90"),
                        "LAP-001",
                        10
                    );

                    when(productRepository.findById(productId))
                            .thenReturn(Optional.of(product));

                    when(cartItemRepository.existsByProductId(productId))
                            .thenReturn(true);

                    when(orderItemRepository.existsByProductId(productId))
                            .thenReturn(false);

                    assertTrue(product.isActive());

                    productService.delete(productId);

                    assertFalse(product.isActive());

                    verify(productRepository).findById(productId);
                    verify(cartItemRepository).existsByProductId(productId);
                    verify(orderItemRepository).existsByProductId(productId);
                    verify(productRepository).save(product);

                    verify(productRepository, never()).delete(any(Product.class));
                }

                @Test // exist on orderitem need to deactivate
                void delete_shouldDeactivateProduct_whenProductHasOrderItems() {
                    Long productId = 1L;

                    Product product = new Product(
                        "Laptop",
                        "Gaming laptop",
                        new Brand(),
                        new Category(),
                        new BigDecimal("4999.90"),
                        "LAP-001",
                        10
                    );

                    when(productRepository.findById(productId))
                            .thenReturn(Optional.of(product));

                    when(cartItemRepository.existsByProductId(productId))
                            .thenReturn(false);

                    when(orderItemRepository.existsByProductId(productId))
                            .thenReturn(true);

                    assertTrue(product.isActive());

                    productService.delete(productId);

                    assertFalse(product.isActive());

                    verify(productRepository).findById(productId);
                    verify(cartItemRepository).existsByProductId(productId);
                    verify(orderItemRepository).existsByProductId(productId);
                    verify(productRepository).save(product);

                    verify(productRepository, never()).delete(any(Product.class));
                }

                @Test // exist in botyh
                void delete_shouldDeactivateProduct_whenProductHasCartItemsAndOrderItems() {
                    Long productId = 1L;

                    Product product = new Product(
                        "Laptop",
                        "Gaming laptop",
                        new Brand(),
                        new Category(),
                        new BigDecimal("4999.90"),
                        "LAP-001",
                        10
                    );

                    when(productRepository.findById(productId))
                            .thenReturn(Optional.of(product));

                    when(cartItemRepository.existsByProductId(productId))
                            .thenReturn(true);

                    when(orderItemRepository.existsByProductId(productId))
                            .thenReturn(true);

                    assertTrue(product.isActive());

                    productService.delete(productId);

                    assertFalse(product.isActive());

                    verify(productRepository).findById(productId);
                    verify(cartItemRepository).existsByProductId(productId);
                    verify(orderItemRepository).existsByProductId(productId);
                    verify(productRepository).save(product);

                    verify(productRepository, never()).delete(any(Product.class));
                }

        



}
