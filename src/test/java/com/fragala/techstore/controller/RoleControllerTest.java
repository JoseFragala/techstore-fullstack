package com.fragala.techstore.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fragala.techstore.dto.response.RoleResponse;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.service.RoleService;

/**
 * Web layer tests for {@link RoleController}.
 *
 * <p>These tests verify the HTTP contract of the role endpoints
 * independently from the database and service implementation.
 */
@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    private RoleResponse roleResponse;

    @BeforeEach
    void setUp() {
        roleResponse = new RoleResponse();

        roleResponse.setId(1L);
        roleResponse.setName("CUSTOMER");
        roleResponse.setDescription("Customer role");
        roleResponse.setCreatedAt(LocalDateTime.of(2026, 9, 11, 10, 0));
    }

    @Test
    void shouldFindRoleById() throws Exception {
        when(roleService.findById(1L))
                .thenReturn(roleResponse);

        mockMvc.perform(get("/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("CUSTOMER"))
                .andExpect(jsonPath("$.description").value("Customer role"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldReturnNotFoundWhenRoleDoesNotExist() throws Exception {
        when(roleService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Role not found with id: 999"));

        mockMvc.perform(get("/roles/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindAllRoles() throws Exception {
        RoleResponse admin = new RoleResponse();
        admin.setId(1L);
        admin.setName("ADMIN");
        admin.setDescription("Administrator role");

        RoleResponse customer = new RoleResponse();
        customer.setId(2L);
        customer.setName("CUSTOMER");
        customer.setDescription("Customer role");

        RoleResponse seller = new RoleResponse();
        seller.setId(3L);
        seller.setName("SELLER");
        seller.setDescription("Seller role");

        when(roleService.findAll())
                .thenReturn(List.of(admin, customer, seller));

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("ADMIN"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("CUSTOMER"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("SELLER"));
    }

    @Test
    void shouldReturnEmptyListWhenNoRolesExist() throws Exception {
        when(roleService.findAll())
                .thenReturn(List.of());

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}