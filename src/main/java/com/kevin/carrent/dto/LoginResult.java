package com.kevin.carrent.dto;

import com.kevin.carrent.enums.Role;

public class LoginResult {
    private final String accessToken;
    private final String refreshToken;
    private final String email;
    private final Role role;

    public LoginResult(
            String accessToken,
            String refreshToken,
            String email,
            Role role
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}
