package com.fragala.techstore.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * JPA entity that represents a user role.
 *
 * <p>This class exists to separate authorization-related role data from the user itself. That
 * helps model concerns such as admin/customer distinctions in a normalized way.
 *
 * <p>Architecturally, this entity belongs to the domain/persistence layer and maps the
 * {@code roles} table.
 *
 * <p>It is used whenever a user needs to be associated with an authorization or responsibility
 * level inside the system.
 */
// `@Entity` makes this class a managed JPA entity.
@Getter
@NoArgsConstructor
@Entity
// `@Table` maps the entity to the `roles` table.
@Table(name = "roles")
public class Role {

    // Primary key used to uniquely identify each role record.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // The role name is unique because the same logical role should not be stored twice.
    @Setter
    @Column(nullable = false, unique = true)
    private String name;

    // The description explains the business meaning of the role in a human-readable way.
    @Setter
    @Column(nullable = false)
    private String description;

    // Hibernate fills this timestamp automatically when the role row is first inserted.
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Creates a new role with the required business fields.
     *
     * <p>This constructor is useful when the application needs to create predefined system roles
     * such as ADMIN, CUSTOMER, and SELLER during startup.
     *
     * @param name the unique role name stored in the database
     * @param description a short explanation of the role's purpose
     */
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
