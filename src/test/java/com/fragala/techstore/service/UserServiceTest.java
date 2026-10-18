package com.fragala.techstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fragala.techstore.dto.request.CreateUserRequest;
import com.fragala.techstore.dto.request.UpdateUserRequest;
import com.fragala.techstore.dto.response.RoleResponse;
import com.fragala.techstore.dto.response.UserResponse;
import com.fragala.techstore.entity.Role;
import com.fragala.techstore.entity.User;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.exception.UserAlreadyExistsException;
import com.fragala.techstore.mapper.UserMapper;
import com.fragala.techstore.repository.RoleRepository;
import com.fragala.techstore.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    private final CreateUserRequest createRequest = new CreateUserRequest(
        "Alice", "alice@example.com", "test-password", "11999990000", 1L);
    private final UpdateUserRequest updateRequest = new UpdateUserRequest(
        "Alice Updated", "updated@example.com", "11999991111", 2L);

    @Test
    void create_shouldCreateUser_whenRequestIsValid() {
        Role role = new Role("CUSTOMER", "Customer role");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(createRequest.password())).thenReturn("encoded-test-password");
        when(userRepository.save(any(User.class))).thenAnswer(call -> call.getArgument(0));
        UserResponse expected = response();
        when(userMapper.toResponse(any(User.class))).thenReturn(expected);

        assertEquals(expected, userService.create(createRequest));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals(createRequest.name(), saved.getName());
        assertEquals(createRequest.email(), saved.getEmail());
        assertEquals(createRequest.phone(), saved.getPhone());
        assertSame(role, saved.getRole());
        assertTrue(saved.isActive());
        assertEquals("encoded-test-password", saved.getPassword());
        assertNotEquals(createRequest.password(), saved.getPassword());
    }

    @Test
    void create_shouldThrowException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail(createRequest.email())).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> userService.create(createRequest));
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(roleRepository, passwordEncoder, userMapper);
    }

    @Test
    void create_shouldThrowException_whenRoleDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> userService.create(createRequest));
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder, userMapper);
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        User user = new User();
        UserResponse expected = response();
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expected);
        assertEquals(expected, userService.findById(5L));
    }

    @Test
    void findById_shouldThrowException_whenUserDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(99L));
        verifyNoInteractions(userMapper);
    }

    @Test
    void findAll_shouldReturnMappedUsers_whenUsersExist() {
        User user = new User();
        UserResponse expected = response();
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(expected);
        assertEquals(List.of(expected), userService.findAll());
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());
        assertTrue(userService.findAll().isEmpty());
        verifyNoInteractions(userMapper);
    }

    @Test
    void update_shouldUpdateUserAndPreservePassword_whenRequestIsValid() {
        User user = existingUser();
        Role role = new Role("SELLER", "Seller role");
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));
        UserResponse expected = response();
        when(userMapper.toResponse(user)).thenReturn(expected);

        assertEquals(expected, userService.update(5L, updateRequest));
        verify(userRepository).existsByEmailAndIdNot(updateRequest.email(), 5L);
        assertEquals(updateRequest.name(), user.getName());
        assertEquals(updateRequest.email(), user.getEmail());
        assertEquals(updateRequest.phone(), user.getPhone());
        assertSame(role, user.getRole());
        assertEquals("original-hash", user.getPassword());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void update_shouldThrowException_whenUserDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> userService.update(99L, updateRequest));
        verifyNoInteractions(roleRepository, userMapper, passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_shouldThrowExceptionWithoutChangingUser_whenEmailAlreadyExists() {
        User user = existingUser();
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(updateRequest.email(), 5L)).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> userService.update(5L, updateRequest));
        assertEquals("Alice", user.getName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("11999990000", user.getPhone());
        assertEquals("original-hash", user.getPassword());
        verifyNoInteractions(roleRepository, userMapper, passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_shouldThrowExceptionWithoutChangingUser_whenRoleDoesNotExist() {
        User user = existingUser();
        Role originalRole = user.getRole();
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        assertThrows(ResourceNotFoundException.class, () -> userService.update(5L, updateRequest));
        assertEquals("Alice", user.getName());
        assertEquals("alice@example.com", user.getEmail());
        assertSame(originalRole, user.getRole());
        verifyNoInteractions(userMapper, passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void activate_shouldActivateUser_whenUserExists() {
        User user = existingUser();
        user.deactivate();
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        userService.activate(5L);
        assertTrue(user.isActive());
    }

    @Test
    void activate_shouldThrowException_whenUserDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> userService.activate(99L));
    }

    @Test
    void deactivate_shouldDeactivateUser_whenUserExists() {
        User user = existingUser();
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        userService.deactivate(5L);
        assertFalse(user.isActive());
    }

    @Test
    void deactivate_shouldThrowException_whenUserDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> userService.deactivate(99L));
    }

    @Test
    void changePassword_shouldEncodePassword_whenUserExists() {
        User user = existingUser();
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-test-password")).thenReturn("new-hash");
        userService.changePassword(5L, "new-test-password");
        assertEquals("new-hash", user.getPassword());
        verify(passwordEncoder).encode("new-test-password");
    }

    @Test
    void changePassword_shouldThrowException_whenUserDoesNotExist() {
        assertThrows(ResourceNotFoundException.class,
            () -> userService.changePassword(99L, "new-test-password"));
        verifyNoInteractions(passwordEncoder);
    }

    private User existingUser() {
        User user = new User();
        user.setName("Alice");
        user.changeEmail("alice@example.com");
        user.changePhone("11999990000");
        user.changePassword("original-hash");
        user.changeRole(new Role("CUSTOMER", "Customer role"));
        return user;
    }

    private UserResponse response() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 10, 18, 12, 0);
        return new UserResponse(5L, "Alice", "alice@example.com", "11999990000",
            true, new RoleResponse(), timestamp, timestamp);
    }
}
