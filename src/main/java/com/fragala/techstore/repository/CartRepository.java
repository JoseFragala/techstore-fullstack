package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.Cart;

/**
 * Repository responsible for database access related to {@link Cart}.
 *
 * <p>This repository exists to centralize cart persistence operations in the data layer instead
 * of scattering query logic across the application.
 *
 * <p>In the architecture, services use this interface to read and write cart data while Spring
 * Data JPA generates the implementation automatically at runtime.
 *
 * <p>Because it extends {@link JpaRepository}, methods like {@code save}, {@code findAll},
 * {@code findById}, {@code delete}, and {@code count} are inherited automatically.
 */
public interface CartRepository extends JpaRepository <Cart, Long> {
    
}
