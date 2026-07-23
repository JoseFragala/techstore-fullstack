package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.User;

public interface UserRepository extends JpaRepository <User, Long> {
    
}
