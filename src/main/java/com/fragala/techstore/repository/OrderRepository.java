package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.Order;

/**
 * Repository responsible for database access related to {@link Order}.
 *
 * <p>This repository exists to provide a dedicated persistence abstraction for order data.
 * Services can depend on it to retrieve and store orders without knowing how the database access
 * is implemented.
 *
 * <p>Within the architecture, this interface belongs to the data access layer and supports the
 * service layer, where order-related business rules should live.
 *
 * <p>By extending {@link JpaRepository}, the project inherits standard methods such as
 * {@code save}, {@code findById}, {@code findAll}, {@code delete}, and {@code flush}.
 */
public interface OrderRepository extends JpaRepository <Order, Long> {
    
}
