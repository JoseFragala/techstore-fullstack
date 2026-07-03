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
private String name;

@Column
@Setter
private String complement;

@Column(nullable = false)
private String neighborhood;

@Column(nullable = false)
private String city;

@Column(nullable = false)
private String state;

@Column(nullable = false)
private String zipCode;

@Column(nullable = false)
private String country;

@Column (nullable = false)
private boolean isDefault;

@CreationTimestamp
@Column (nullable = false, updatable = false)
private LocalDateTime createdAt;

@UpdateTimestamp   
@Column(nullable = false)
private LocalDateTime updatedAt;

    
}
