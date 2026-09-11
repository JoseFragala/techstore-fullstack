package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    boolean existsByProductId(Long productId);
    
}
