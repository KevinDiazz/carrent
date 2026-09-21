package com.kevin.carrent.dto;

import com.kevin.carrent.enums.Role;

public class LoginResponse {

    private final String email;
    private final Role role;
    private final String name;

    public LoginResponse(String email, Role role, String name) {
        this.email = email;
        this.role = role;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public String getName() {
        return name;
    }
}