package com.example.recipes.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class UserCredentialsDto {
    @NotBlank
    @Email
    private String email;
    private String password;
    private boolean emailVerified;
    private Set<String> roles;

    public UserCredentialsDto(String email, String password, boolean emailVerified, Set<String> roles) {
        this.email = email;
        this.password = password;
        this.emailVerified = emailVerified;
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }
}
