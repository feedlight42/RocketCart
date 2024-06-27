package com.example.RocketCart.model;

import java.util.List;

public class AuthResponse {

    private final String jwt;
    private List<String> roles;

    public AuthResponse(String jwt, List<String> roles) {

        this.jwt = jwt;
        this.roles = roles;
    }

    public String getJwt() {
        return jwt;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}

