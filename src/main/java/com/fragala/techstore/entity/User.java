package com.fragala.techstore.entity;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity that represents an application user or customer.
 *
 * <p>This class exists to store the core identity and account data required for a person who uses
 * the system, including contact information, credentials, role, cart, addresses, and orders.
 *
 * <p>Architecturally, this entity is a central part of the domain/persistence layer. It maps the
 * {@code users} table and acts as a parent for several related concepts in the e-commerce model.
 *
 * <p>It is used whenever user account data must be persisted or linked to business operations.
 */
// `@Entity` tells JPA to map this class as a database entity.
@Entity
@Getter
@NoArgsConstructor
// `@Table` specifies the table name used for persistence.
@Table(name = "users")
public class User {


    // Primary key that uniquely identifies each user.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column (nullable = false)
    private String name;

    @Column (nullable = false, unique = true)
    private String email;

    @Column (nullable = false)
    private String password;

    @Column (nullable = false)
    private String phone;

    @Column (nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many users can share the same role, such as CUSTOMER or ADMIN.
    // This entity owns the relationship because the foreign key `role_id` is stored in `users`.
    @JoinColumn(name = "role_id", nullable = false)
    @ManyToOne
    private Role role;

    // One user has one cart in this model.
    // `mappedBy = "user"` means `Cart` owns the relationship because the foreign key is stored
    // on the cart table, not on the user table.
    @OneToOne(mappedBy = "user")
    private Cart cart;

    // One user can save multiple addresses.
    // `mappedBy = "user"` marks this as the inverse side because the foreign key lives in `Address`.
    @OneToMany(mappedBy = "user")
    private List<Address> addresses;

    // One user can place many orders over time.
    // `mappedBy = "user"` indicates the `Order` entity owns the relationship through its foreign key.
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
    
    /**
     * Activates the user account.
     *
     * <p>Business responsibility: enable the account without exposing direct field mutation to
     * callers.
     */

    public void activate(){
        this.active = true;
    }

    /**
     * Deactivates the user account.
     *
     * <p>Business responsibility: disable the account while preserving the user record.
     */
    public void deactivate(){
        this.active = false;
    }

    /**
     * Replaces the stored password with a new hashed password.
     *
     * <p>Business responsibility: encapsulate credential updates in a domain method. The method
     * expects a hashed password rather than a plain text password.
     *
     * @param hashedPassword the already-hashed password value to store
     */
    public void changePassword(String hashedPassword){
        this.password = hashedPassword;
    }

    /**
     * Updates the user's email address.
     *
     * <p>Business responsibility: centralize email changes in the entity.
     *
     * @param email the new email address
     */
    public void changeEmail(String email){
        this.email = email;
    }

    /**
     * Changes the role assigned to the user.
     *
     * <p>Business responsibility: update the authorization-related relationship for the user.
     *
     * @param role the new role to associate with the user
     */
    public void changeRole(Role role){
        this.role = role;
    }

    /**
     * Updates the user's phone number.
     *
     * <p>Business responsibility: centralize phone changes in the entity.
     *
     * @param phone the new phone number
     */
    public void changePhone(String phone){
        this.phone = phone;
    }
}
