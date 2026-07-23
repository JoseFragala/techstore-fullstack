package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.Brand;

public interface BrandRepository extends JpaRepository <Brand, Long> {
    
}
