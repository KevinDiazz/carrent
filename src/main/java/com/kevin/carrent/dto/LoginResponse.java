package com.kevin.carrent.dto;

import com.kevin.carrent.enums.Role;

public class LoginResponse {

    private final String token;
    private final String email;
    private final Role role;

    public LoginResponse(String token, String email, Role role) {
        this.token = token;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}