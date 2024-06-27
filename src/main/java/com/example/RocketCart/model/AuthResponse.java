package com.example.RocketCart.model;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public class AuthResponse {

    private final String jwt;
    private List<String> roles;
    private UserDetails userDetails;

    public AuthResponse(String jwt, List<String> roles, UserDetails userDetails) {

        this.jwt = jwt;
        this.roles = roles;
        this.userDetails = userDetails;
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

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }
}

