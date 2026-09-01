package com.fragala.techstore.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity that represents a physical address stored for a user.
 *
 * <p>This class exists so the application can persist one or more addresses linked to a user,
 * which is common in e-commerce scenarios for billing, shipping, or profile management.
 *
 * <p>Architecturally, this entity belongs to the persistence/domain model layer. It maps the
 * {@code addresses} table and participates in the relationship model defined by JPA.
 *
 * <p>It is used whenever address data needs to be stored, retrieved, or associated with a user.
 */
// `@Entity` tells JPA that this class should be managed as a persistent entity.
// It is used here so Hibernate can map this object to a database table.
@Entity
@Getter
@NoArgsConstructor
// `@Table` customizes the table name used in the database.
// It is used here to map this entity explicitly to the `addresses` table.
@Table(name = "addresses")
public class Address {

// `@ManyToOne` means many addresses can belong to one user.
// This relationship exists because a single user may store multiple addresses over time.
@ManyToOne
// `@JoinColumn` defines the foreign key column stored in this table.
// This entity owns the relationship because the `user_id` column lives in the `addresses` table.
@Setter
@JoinColumn(name = "user_id", nullable = false)
private User user;

// `@Id` marks the primary key of the entity.
// A primary key uniquely identifies each row in the table.
@Id
// `@GeneratedValue` tells JPA to let the database generate the identifier value.
// `GenerationType.IDENTITY` is commonly used with auto-increment columns.
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(nullable = false)
private Long id;

// This field gives a friendly label to the address, such as "Home" or "Work",
// so users can distinguish between multiple saved addresses.
@Column(nullable = false) // after we'll use to name the adresses (home, work, other.)
@Setter
private String name;

@Column(nullable = false)
@Setter
private String street;

@Column(nullable = false)
@Setter
private String number;

@Column
@Setter
private String complement;

@Column(nullable = false)
@Setter
private String neighborhood;

@Column(nullable = false)
@Setter
private String city;

@Column(nullable = false)
@Setter
private String state;

@Column(name = "zip_code", nullable = false)
@Setter
private String zipCode;

@Column(nullable = false)
@Setter
private String country;

@Column (name = "is_default", nullable = false)
@Setter
private boolean defaultAddress;

// `@CreationTimestamp` lets Hibernate fill this field automatically when the row is first inserted.
@CreationTimestamp
@Column (name = "created_at",nullable = false, updatable = false)
private LocalDateTime createdAt;

// `@UpdateTimestamp` lets Hibernate update this field whenever the entity is modified.
@UpdateTimestamp   
@Column(name = "updated_at")
private LocalDateTime updatedAt;


    
}
