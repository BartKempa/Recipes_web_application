package com.example.recipes.domain.user.dto;

import com.example.recipes.domain.user.validation.PasswordCriteria;
import com.example.recipes.domain.user.validation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRetrievePasswordDto {
    private Long id;
    @NotBlank
    @PasswordCriteria
    private String password;
    private String token;

    public UserRetrievePasswordDto(Long id, String password, String token) {
        this.id = id;
        this.password = password;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
