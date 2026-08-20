package com.kevin.carrent.service;

import com.kevin.carrent.dto.RegisterRequest;
import com.kevin.carrent.dto.RegisterResponse;
import com.kevin.carrent.entity.User;
import com.kevin.carrent.enums.Role;
import com.kevin.carrent.mapper.UserMapper;
import com.kevin.carrent.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public RegisterResponse registerUser(RegisterRequest request) {
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}
