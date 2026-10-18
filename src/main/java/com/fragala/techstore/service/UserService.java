package com.fragala.techstore.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.fragala.techstore.dto.request.CreateUserRequest;
import com.fragala.techstore.dto.request.UpdateUserRequest;
import com.fragala.techstore.dto.response.UserResponse;
import com.fragala.techstore.entity.Role;
import com.fragala.techstore.entity.User;
import com.fragala.techstore.exception.ResourceNotFoundException;
import com.fragala.techstore.exception.UserAlreadyExistsException;
import com.fragala.techstore.mapper.UserMapper;
import com.fragala.techstore.repository.RoleRepository;
import com.fragala.techstore.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already registered: " + request.email());
        }

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.roleId()));

        User user = new User();
        user.setName(request.name());
        user.changeEmail(request.email());
        user.changePassword(passwordEncoder.encode(request.password()));
        user.changePhone(request.phone());
        user.changeRole(role);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return userMapper.toResponse(user);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new UserAlreadyExistsException("Email already registered: " + request.email());
        }

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.roleId()));

        user.setName(request.name());
        user.changeEmail(request.email());
        user.changePhone(request.phone());
        user.changeRole(role);

        return userMapper.toResponse(user);
    }

    @Transactional
    public void activate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.activate();
    }

    @Transactional
    public void deactivate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.deactivate();
    }

    @Transactional
    public void changePassword(Long id, String rawPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.changePassword(passwordEncoder.encode(rawPassword));
    }
}
