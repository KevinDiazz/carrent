package com.kevin.carrent.controller;

import com.kevin.carrent.dto.*;
import com.kevin.carrent.entity.RefreshToken;
import com.kevin.carrent.entity.User;
import com.kevin.carrent.repository.RefreshTokenRepository;
import com.kevin.carrent.service.JwtService;
import com.kevin.carrent.service.RefreshTokenService;
import com.kevin.carrent.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserController(UserService userService, RefreshTokenService refreshTokenService, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResult loginResult = userService.login(request);

        Cookie cookie = new Cookie(
                "access_token",
                loginResult.getAccessToken()
        );

        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false);

        response.addCookie(cookie);
        Cookie refreshCookie = new Cookie(
                "refresh_token",
                loginResult.getRefreshToken()
        );

        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setSecure(false);

        response.addCookie(refreshCookie);
        LoginResponse loginResponse = new LoginResponse(
                loginResult.getEmail(),
                loginResult.getRole()
        );

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {

                if ("refresh_token".equals(cookie.getName())) {

                    String refreshToken = cookie.getValue();

                    refreshTokenRepository.findByToken(refreshToken)
                            .ifPresent(refreshTokenRepository::delete);
                }
            }
        }

        Cookie accessCookie = new Cookie("access_token", null);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setSecure(false);
        accessCookie.setMaxAge(0);

        response.addCookie(accessCookie);


        Cookie refreshCookie = new Cookie("refresh_token", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setSecure(false);
        refreshCookie.setMaxAge(0);

        response.addCookie(refreshCookie);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Cookie[] cookies = request.getCookies();

        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        RefreshToken currentRefreshToken =
                refreshTokenService.findValidToken(refreshToken);

        User user = currentRefreshToken.getUser();

        String accessToken = jwtService.generateToken(user);

        RefreshToken newRefreshToken =
                refreshTokenService.rotate(currentRefreshToken);

        Cookie accessCookie = new Cookie(
                "access_token",
                accessToken
        );

        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setSecure(false);

        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie(
                "refresh_token",
                newRefreshToken.getToken()
        );

        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setSecure(false);

        response.addCookie(refreshCookie);

        LoginResponse loginResponse = new LoginResponse(
                user.getEmail(),
                user.getRole()
        );

        return ResponseEntity.ok(loginResponse);
    }
}
