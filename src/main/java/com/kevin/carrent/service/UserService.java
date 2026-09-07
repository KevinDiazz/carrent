package com.kevin.carrent.service;

import com.kevin.carrent.dto.*;
import com.kevin.carrent.entity.RefreshToken;
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
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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

    public LoginResult login(@Valid LoginRequest request) {
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
        String accessToken = jwtService.generateToken(user);

        RefreshToken refreshToken = refreshTokenService.create(user);

        return new LoginResult(
                accessToken,
                refreshToken.getToken(),
                user.getEmail(),
                user.getRole()
        );
    }
}
