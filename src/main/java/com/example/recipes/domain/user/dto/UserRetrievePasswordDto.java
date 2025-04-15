package com.example.recipes.domain.user.dto;

import com.example.recipes.domain.user.validation.PasswordCriteria;
import com.example.recipes.domain.user.validation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRetrievePasswordDto {
    private Long id;
    @NotBlank
    @Email
    @UniqueEmail
    private String email;
    @NotBlank
    @PasswordCriteria
    private String password;

    public UserRetrievePasswordDto(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public UserRetrievePasswordDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
