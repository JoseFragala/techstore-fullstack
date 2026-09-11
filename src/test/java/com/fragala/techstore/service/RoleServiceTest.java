package com.fragala.techstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fragala.techstore.dto.response.RoleResponse;
import com.fragala.techstore.entity.Role;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.mapper.RoleMapper;
import com.fragala.techstore.repository.RoleRepository;

/**
 * Unit tests for {@link RoleService}.
 *
 * <p>These tests verify the service behavior independently from the database
 * and HTTP layer.
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private RoleResponse roleResponse;

    @BeforeEach
    void setUp() {
        role = new Role("CUSTOMER", "Customer role");

        roleResponse = new RoleResponse();
        roleResponse.setId(1L);
        roleResponse.setName("CUSTOMER");
        roleResponse.setDescription("Customer role");
        roleResponse.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldFindRoleById() {
        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(roleMapper.toResponse(role))
                .thenReturn(roleResponse);

        RoleResponse result = roleService.findById(1L);

        assertEquals(roleResponse, result);

        verify(roleRepository).findById(1L);
        verify(roleMapper).toResponse(role);
    }

    @Test
    void shouldThrowExceptionWhenRoleDoesNotExist() {
        when(roleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.findById(1L)
        );

        verify(roleRepository).findById(1L);
    }

    @Test
    void shouldFindAllRoles() {
        Role admin = new Role("ADMIN", "Administrator role");
        Role customer = new Role("CUSTOMER", "Customer role");
        Role seller = new Role("SELLER", "Seller role");

        RoleResponse adminResponse = new RoleResponse();
        adminResponse.setId(1L);
        adminResponse.setName("ADMIN");
        adminResponse.setDescription("Administrator role");

        RoleResponse customerResponse = new RoleResponse();
        customerResponse.setId(2L);
        customerResponse.setName("CUSTOMER");
        customerResponse.setDescription("Customer role");

        RoleResponse sellerResponse = new RoleResponse();
        sellerResponse.setId(3L);
        sellerResponse.setName("SELLER");
        sellerResponse.setDescription("Seller role");

        when(roleRepository.findAll())
                .thenReturn(List.of(admin, customer, seller));

        when(roleMapper.toResponse(admin))
                .thenReturn(adminResponse);

        when(roleMapper.toResponse(customer))
                .thenReturn(customerResponse);

        when(roleMapper.toResponse(seller))
                .thenReturn(sellerResponse);

        List<RoleResponse> result = roleService.findAll();

        assertEquals(3, result.size());
        assertEquals("ADMIN", result.get(0).getName());
        assertEquals("CUSTOMER", result.get(1).getName());
        assertEquals("SELLER", result.get(2).getName());

        verify(roleRepository).findAll();
        verify(roleMapper).toResponse(admin);
        verify(roleMapper).toResponse(customer);
        verify(roleMapper).toResponse(seller);
    }

    @Test
    void shouldReturnEmptyListWhenNoRolesExist() {
        when(roleRepository.findAll())
                .thenReturn(List.of());

        List<RoleResponse> result = roleService.findAll();

        assertEquals(0, result.size());

        verify(roleRepository).findAll();
    }
}