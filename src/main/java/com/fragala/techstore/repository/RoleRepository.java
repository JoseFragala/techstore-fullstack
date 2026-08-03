package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.Role;

/**
 * Repository responsible for database access related to {@link Role}.
 *
 * <p>This interface exists so the application can query and persist authorization roles through
 * Spring Data JPA instead of writing manual SQL for every role operation.
 *
 * <p>Architecturally, it belongs to the repository layer and is especially useful during
 * application startup, when the system needs to guarantee that required roles already exist.
 *
 * <p>Extending {@link JpaRepository} provides inherited CRUD operations, while custom derived
 * query methods such as {@link #existsByName(String)} let us keep the initializer logic simple
 * and expressive.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Checks whether a role with the given name already exists.
     *
     * <p>This is used by the startup initializer so role creation stays idempotent and duplicate
     * rows are not inserted on every application restart.
     *
     * @param name the unique role name to look for
     * @return {@code true} when a role with that name already exists
     */
    boolean existsByName(String name);
}
