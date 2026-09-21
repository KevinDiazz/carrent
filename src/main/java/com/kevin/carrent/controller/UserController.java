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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${app.cookie-secure}")
    private boolean cookieSecure;

    @Value("${app.cookie-same-site}")
    private String cookieSameSite;

    private ResponseCookie createAuthCookie(
            String name,
            String value,
            long maxAge
    ) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .sameSite(cookieSameSite)
                .maxAge(maxAge)
                .build();
    }

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

        ResponseCookie accessCookie = createAuthCookie(
                "access_token",
                loginResult.getAccessToken(),
                15 * 60
        );

        ResponseCookie refreshCookie = createAuthCookie(
                "refresh_token",
                loginResult.getRefreshToken(),
                7 * 24 * 60 * 60
        );

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        LoginResponse loginResponse = new LoginResponse(
                loginResult.getEmail(),
                loginResult.getRole(),
                loginResult.getName()
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
        ResponseCookie accessCookie = createAuthCookie(
                "access_token",
                "",
                0
        );

        ResponseCookie refreshCookie = createAuthCookie(
                "refresh_token",
                "",
                0
        );

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

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

        ResponseCookie accessCookie = createAuthCookie(
                "access_token",
                accessToken,
                15 * 60
        );

        ResponseCookie refreshCookie = createAuthCookie(
                "refresh_token",
                newRefreshToken.getToken(),
                7 * 24 * 60 * 60
        );

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        LoginResponse loginResponse = new LoginResponse(
                user.getEmail(),
                user.getRole(),
                user.getName()
        );

        return ResponseEntity.ok(loginResponse);
    }
}
