package com.fragala.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.Address;
public interface AddressRepository extends JpaRepository<Address, Long>{
    
}
