package com.fragala.techstore.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fragala.techstore.dto.request.CreateUserRequest;
import com.fragala.techstore.dto.request.UpdateUserRequest;
import com.fragala.techstore.dto.response.RoleResponse;
import com.fragala.techstore.dto.response.UserResponse;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.exception.UserAlreadyExistsException;
import com.fragala.techstore.service.UserService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;

    private final CreateUserRequest createRequest = new CreateUserRequest(
        "Alice", "alice@example.com", "test-password", "11999990000", 1L);
    private final UpdateUserRequest updateRequest = new UpdateUserRequest(
        "Alice Updated", "updated@example.com", "11999991111", 1L);

    @Test
    void create_shouldReturn201WithoutPassword_whenRequestIsValid() throws Exception {
        when(userService.create(createRequest)).thenReturn(response());
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.email").value("alice@example.com"))
            .andExpect(jsonPath("$.role.name").value("CUSTOMER"))
            .andExpect(jsonPath("$.password").doesNotExist());
        verify(userService).create(createRequest);
    }

    @ParameterizedTest
    @MethodSource("invalidCreateRequests")
    void create_shouldReturn400WithoutCallingService_whenRequestIsInvalid(CreateUserRequest request)
            throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.errors").isMap());
        verifyNoInteractions(userService);
    }

    @Test
    void create_shouldReturn409_whenEmailAlreadyExists() throws Exception {
        when(userService.create(createRequest))
            .thenThrow(new UserAlreadyExistsException("Email already registered"));
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    void create_shouldReturn404_whenRoleDoesNotExist() throws Exception {
        when(userService.create(createRequest)).thenThrow(new ResourceNotFoundException("Role not found"));
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Role not found"));
    }

    @Test
    void findById_shouldReturn200WithoutPassword_whenUserExists() throws Exception {
        when(userService.findById(5L)).thenReturn(response());
        mockMvc.perform(get("/users/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(jsonPath("$.active").value(true))
            .andExpect(jsonPath("$.password").doesNotExist());
        verify(userService).findById(5L);
    }

    @Test
    void findById_shouldReturn404_whenUserDoesNotExist() throws Exception {
        when(userService.findById(99L)).thenThrow(new ResourceNotFoundException("User not found"));
        mockMvc.perform(get("/users/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void findAll_shouldReturn200WithoutPasswords_whenUsersExist() throws Exception {
        when(userService.findAll()).thenReturn(List.of(response()));
        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].email").value("alice@example.com"))
            .andExpect(jsonPath("$[0].password").doesNotExist());
        verify(userService).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoUsersExist() throws Exception {
        when(userService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/users"))
            .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void update_shouldReturn200_whenRequestIsValid() throws Exception {
        UserResponse updated = new UserResponse(5L, updateRequest.name(), updateRequest.email(),
            updateRequest.phone(), true, response().role(), response().createdAt(), response().updatedAt());
        when(userService.update(5L, updateRequest)).thenReturn(updated);
        mockMvc.perform(put("/users/5").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(updateRequest.name()))
            .andExpect(jsonPath("$.email").value(updateRequest.email()))
            .andExpect(jsonPath("$.password").doesNotExist());
        verify(userService).update(5L, updateRequest);
    }

    @ParameterizedTest
    @MethodSource("invalidUpdateRequests")
    void update_shouldReturn400WithoutCallingService_whenRequestIsInvalid(UpdateUserRequest request)
            throws Exception {
        mockMvc.perform(put("/users/5").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isMap());
        verifyNoInteractions(userService);
    }

    @Test
    void update_shouldReturn409_whenEmailAlreadyExists() throws Exception {
        when(userService.update(5L, updateRequest))
            .thenThrow(new UserAlreadyExistsException("Email already registered"));
        mockMvc.perform(put("/users/5").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409));
    }

    @ParameterizedTest
    @ValueSource(strings = {"User not found", "Role not found"})
    void update_shouldReturn404_whenReferencedResourceDoesNotExist(String message) throws Exception {
        when(userService.update(5L, updateRequest)).thenThrow(new ResourceNotFoundException(message));
        mockMvc.perform(put("/users/5").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {"activate", "deactivate"})
    void changeActiveState_shouldReturn204_whenUserExists(String operation) throws Exception {
        mockMvc.perform(patch("/users/5/" + operation))
            .andExpect(status().isNoContent()).andExpect(content().string(""));
        if (operation.equals("activate")) {
            verify(userService).activate(5L);
        } else {
            verify(userService).deactivate(5L);
        }
        verifyNoMoreInteractions(userService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"activate", "deactivate"})
    void changeActiveState_shouldReturn404_whenUserDoesNotExist(String operation) throws Exception {
        if (operation.equals("activate")) {
            doThrow(new ResourceNotFoundException("User not found")).when(userService).activate(99L);
        } else {
            doThrow(new ResourceNotFoundException("User not found")).when(userService).deactivate(99L);
        }
        mockMvc.perform(patch("/users/99/" + operation))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User not found"));
    }

    private static Stream<CreateUserRequest> invalidCreateRequests() {
        return Stream.of(
            new CreateUserRequest(" ", "alice@example.com", "test-password", "11999990000", 1L),
            new CreateUserRequest("Alice", "invalid", "test-password", "11999990000", 1L),
            new CreateUserRequest("Alice", "", "test-password", "11999990000", 1L),
            new CreateUserRequest("Alice", "alice@example.com", " ", "11999990000", 1L),
            new CreateUserRequest("Alice", "alice@example.com", "test-password", " ", 1L),
            new CreateUserRequest("Alice", "alice@example.com", "test-password", "11999990000", null));
    }

    private static Stream<UpdateUserRequest> invalidUpdateRequests() {
        return Stream.of(
            new UpdateUserRequest(" ", "alice@example.com", "11999990000", 1L),
            new UpdateUserRequest("Alice", "invalid", "11999990000", 1L),
            new UpdateUserRequest("Alice", "", "11999990000", 1L),
            new UpdateUserRequest("Alice", "alice@example.com", " ", 1L),
            new UpdateUserRequest("Alice", "alice@example.com", "11999990000", null));
    }

    private UserResponse response() {
        RoleResponse role = new RoleResponse();
        role.setId(1L);
        role.setName("CUSTOMER");
        LocalDateTime timestamp = LocalDateTime.of(2026, 10, 18, 12, 0);
        return new UserResponse(5L, "Alice", "alice@example.com", "11999990000",
            true, role, timestamp, timestamp);
    }
}
