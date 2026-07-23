package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.Category;

public interface CategoryRepository extends JpaRepository <Category, Long> {
    
}
