package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.Brand;

/**
 * Repository responsible for database access related to {@link Brand}.
 *
 * <p>This interface represents the persistence gateway for the brand aggregate. It exists so the
 * service layer can work with brands through an abstraction instead of writing SQL directly.
 *
 * <p>Architecturally, repositories belong to the data access layer. They are used by services to
 * load and persist entities while keeping controllers thin and focused on HTTP concerns.
 *
 * <p>By extending {@link JpaRepository}, this repository automatically inherits common CRUD
 * operations such as {@code save}, {@code findById}, {@code findAll}, {@code deleteById}, and
 * {@code existsById}.
 */
public interface BrandRepository extends JpaRepository <Brand, Long> {
    
}
