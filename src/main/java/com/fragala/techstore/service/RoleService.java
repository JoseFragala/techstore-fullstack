package com.fragala.techstore.service;
import com.fragala.techstore.repository.RoleRepository;
import com.fragala.techstore.mapper.RoleMapper;
import com.fragala.techstore.dto.response.RoleResponse;
import com.fragala.techstore.entity.Role;
import com.fragala.techstore.exception.ResourceNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;





@Service 
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public RoleResponse findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        
        return roleMapper.toResponse(role);
    }

    public List<RoleResponse> findAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }
    

}
