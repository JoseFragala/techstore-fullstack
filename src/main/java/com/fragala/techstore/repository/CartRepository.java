package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.Cart;

public interface CartRepository extends JpaRepository <Cart, Long> {
    
}
