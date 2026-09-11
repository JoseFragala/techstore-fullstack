package com.fragala.techstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fragala.techstore.dto.request.CreateProductImageRequest;
import com.fragala.techstore.dto.request.CreateProductRequest;
import com.fragala.techstore.dto.request.UpdateProductRequest;
import com.fragala.techstore.dto.response.ProductResponse;
import com.fragala.techstore.entity.Brand;
import com.fragala.techstore.entity.Category;
import com.fragala.techstore.entity.Product;
import com.fragala.techstore.entity.ProductImage;
import com.fragala.techstore.exception.ProductAlreadyExistsException;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.mapper.ProductMapper;
import com.fragala.techstore.repository.BrandRepository;
import com.fragala.techstore.repository.CartItemRepository;
import com.fragala.techstore.repository.OrderItemRepository;
import com.fragala.techstore.repository.CategoryRepository;
import com.fragala.techstore.repository.ProductRepository;

@Service 
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductService(
        ProductRepository productRepository,
        ProductMapper productMapper, BrandRepository brandRepository, CategoryRepository categoryRepository, CartItemRepository cartItemRepository, OrderItemRepository orderItemRepository){
            this.productRepository = productRepository;
            this.productMapper = productMapper;
            this.brandRepository = brandRepository;
            this.categoryRepository = categoryRepository;
            this.cartItemRepository = cartItemRepository;
            this.orderItemRepository = orderItemRepository;
        }
    public ProductResponse create(CreateProductRequest request){
        Brand brand = brandRepository.findById(request.getBrandId())
            .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (productRepository.existsBySku(request.getSku())) {
            throw new ProductAlreadyExistsException("This SKU already exists");
        }
        Product product = productMapper.toEntity(request, brand, category);

        if (request.getImages() != null){

            if (request.getImages().size() > 4) {
                throw new IllegalArgumentException("A product can have a maximum of 4 images");
            }

            for (CreateProductImageRequest imageRequest : request.getImages()) {
                
                ProductImage image = productMapper.toImageEntity(imageRequest);

                product.addImage(image);
            }
        }
        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    public ProductResponse update(Long id, UpdateProductRequest request){

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Brand brand = brandRepository.findById(request.getBrandId())
            .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);

    
    }

    public ProductResponse findById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        return productMapper.toResponse(product);

    }
    public List<ProductResponse> findAll(){
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }
    public void deactivate(Long id){

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.deactivate();

        productRepository.save(product);
    }

    public void activate(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.activate();
        productRepository.save(product);
    }

    public void delete(Long id){
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        boolean hasCartItems = cartItemRepository.existsByProductId(id);
        boolean hasOrderItems = orderItemRepository.existsByProductId(id);

        if (hasCartItems || hasOrderItems) {
            product.deactivate();
            productRepository.save(product);
            return;
        
        }

        productRepository.delete(product);
    }










    
}
