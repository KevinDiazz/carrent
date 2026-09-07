package com.kevin.carrent.dto;

import com.kevin.carrent.enums.Role;

public class LoginResponse {

    private final String email;
    private final Role role;

    public LoginResponse(String email, Role role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}