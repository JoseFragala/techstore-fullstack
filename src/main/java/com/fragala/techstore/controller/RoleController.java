package com.fragala.techstore.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fragala.techstore.dto.response.RoleResponse;
import com.fragala.techstore.service.RoleService;


@RestController 
@RequestMapping("/roles")
public class RoleController {
    

    private final RoleService roleService;

    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }

    @GetMapping("/{id}")
        public ResponseEntity<RoleResponse> findById(@PathVariable Long id) {
            return ResponseEntity.ok(roleService.findById(id));
        }


    
    @GetMapping 
    public ResponseEntity<List<RoleResponse>> findAll(){
        return ResponseEntity.ok(roleService.findAll());
    }
}
