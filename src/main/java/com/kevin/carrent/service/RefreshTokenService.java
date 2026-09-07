package com.kevin.carrent.service;

import com.kevin.carrent.entity.RefreshToken;
import com.kevin.carrent.entity.User;
import com.kevin.carrent.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private static final long REFRESH_TOKEN_EXPIRATION_SECONDS =
            7 * 24 * 60 * 60;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken create(User user) {

        String token = generateToken();

        Instant expiresAt = Instant.now()
                .plusSeconds(REFRESH_TOKEN_EXPIRATION_SECONDS);

        RefreshToken refreshToken = new RefreshToken(
                token,
                user,
                expiresAt
        );

        return refreshTokenRepository.save(refreshToken);
    }

    private String generateToken() {

        byte[] randomBytes = new byte[32];

        new SecureRandom().nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public RefreshToken findValidToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);

            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    public User getUserFromRefreshToken(String token) {

        RefreshToken refreshToken = findValidToken(token);

        return refreshToken.getUser();
    }
    public RefreshToken rotate(RefreshToken currentToken) {

        refreshTokenRepository.delete(currentToken);

        return create(currentToken.getUser());
    }
}