package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.Product;

/**
 * Repository responsible for database access related to {@link Product}.
 *
 * <p>This interface represents the persistence boundary for products. It exists so services can
 * perform product-related database operations through a clean, testable abstraction.
 *
 * <p>Architecturally, it is part of the repository layer and is intended to be consumed by
 * services rather than directly by controllers.
 *
 * <p>Spring Data JPA automatically provides inherited CRUD methods from {@link JpaRepository},
 * including {@code save}, {@code findById}, {@code findAll}, {@code deleteById}, and sorting or
 * pagination support.
 */
public interface ProductRepository extends JpaRepository <Product, Long> {
    
}
