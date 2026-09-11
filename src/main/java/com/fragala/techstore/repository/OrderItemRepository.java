package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository <OrderItem, Long> {

    boolean existsByProductId(Long productId);
    
}
