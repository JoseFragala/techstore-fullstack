package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.User;

/**
 * Repository responsible for database access related to {@link User}.
 *
 * <p>This interface exists so user persistence is handled in a dedicated data access component
 * instead of being mixed with business rules or HTTP concerns.
 *
 * <p>Within the application architecture, services inject this repository when they need to
 * execute user-related persistence operations.
 *
 * <p>Because it extends {@link JpaRepository}, Spring Data JPA supplies common CRUD methods such
 * as {@code save}, {@code findById}, {@code findAll}, {@code deleteById}, and {@code existsById}
 * without additional implementation code.
 */
public interface UserRepository extends JpaRepository <User, Long> {
    
}
