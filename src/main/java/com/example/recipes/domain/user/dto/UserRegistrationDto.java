package com.example.recipes.domain.user.dto;

import java.util.Set;

public class UserRegistrationDto {
    private Long id;
    private String email;
    private String password;

    public UserRegistrationDto(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public UserRegistrationDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
