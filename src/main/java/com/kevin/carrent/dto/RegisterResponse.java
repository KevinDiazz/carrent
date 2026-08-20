package com.kevin.carrent.dto;

import com.kevin.carrent.enums.Role;

public class RegisterResponse {
    private final String name;
    private final String email;
    private final Role role;

    public RegisterResponse(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getName() {
        return name;
    }


    public String getEmail() {
        return email;
    }


    public Role getRole() {
        return role;
    }

}
