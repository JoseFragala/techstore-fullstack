package com.fragala.techstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fragala.techstore.entity.Address;
public interface AddressRepository extends JpaRepository<Address, Long>{
    
    Optional<Address> findByUser_IdAndDefaultAddressTrue(Long userId);

}
