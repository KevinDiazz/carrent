package com.kevin.carrent.service;

import com.kevin.carrent.dto.LoginRequest;
import com.kevin.carrent.dto.LoginResponse;
import com.kevin.carrent.dto.RegisterRequest;
import com.kevin.carrent.dto.RegisterResponse;
import com.kevin.carrent.entity.User;
import com.kevin.carrent.enums.Role;
import com.kevin.carrent.exception.EmailAlreadyExistsException;
import com.kevin.carrent.exception.InvalidCredentialsException;
import com.kevin.carrent.mapper.UserMapper;
import com.kevin.carrent.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
    }

    public RegisterResponse registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email " + request.getEmail() + " is already registered");
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public LoginResponse login(@Valid LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }
        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole()
        );
    }
}
