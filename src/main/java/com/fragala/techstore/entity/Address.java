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

@Entity
@Getter
@NoArgsConstructor
@Table(name = "addresses")
public class Address {

@ManyToOne
@JoinColumn(name = "user_id", nullable = false)
private User user;

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(nullable = false)
private Long id;

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
private boolean isDefault;

@CreationTimestamp
@Column (name = "created_at",nullable = false, updatable = false)
private LocalDateTime createdAt;

@UpdateTimestamp   
@Column(name = "updated_at")
private LocalDateTime updatedAt;

    
}
