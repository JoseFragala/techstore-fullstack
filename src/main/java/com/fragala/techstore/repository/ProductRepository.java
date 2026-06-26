package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    }

