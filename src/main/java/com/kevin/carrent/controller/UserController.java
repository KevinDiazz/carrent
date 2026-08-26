package com.kevin.carrent.controller;

import com.kevin.carrent.dto.LoginRequest;
import com.kevin.carrent.dto.LoginResponse;
import com.kevin.carrent.dto.RegisterRequest;
import com.kevin.carrent.dto.RegisterResponse;
import com.kevin.carrent.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
