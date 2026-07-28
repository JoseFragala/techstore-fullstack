package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.Category;

/**
 * Repository responsible for database access related to {@link Category}.
 *
 * <p>This interface exists so category persistence can be handled through Spring Data JPA rather
 * than manual DAO code. That reduces boilerplate and keeps the code easier to maintain.
 *
 * <p>Architecturally, it belongs to the repository layer and is normally injected into services,
 * where business logic decides when categories should be created, updated, or queried.
 *
 * <p>Extending {@link JpaRepository} provides inherited CRUD behavior such as {@code save},
 * {@code findById}, {@code findAll}, {@code deleteById}, and pagination support.
 */
public interface CategoryRepository extends JpaRepository <Category, Long> {
    
}
