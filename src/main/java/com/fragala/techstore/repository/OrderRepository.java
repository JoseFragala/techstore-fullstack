package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.Order;

public interface OrderRepository extends JpaRepository <Order, Long> {
    
}
