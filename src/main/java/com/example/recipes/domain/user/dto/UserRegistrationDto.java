package com.example.recipes.domain.user.dto;

import com.example.recipes.domain.user.validation.PasswordCriteria;
import com.example.recipes.domain.user.validation.UniqueEmail;
import jakarta.validation.constraints.*;

import java.util.Set;

public class UserRegistrationDto {
    private Long id;
    @NotBlank
    @Email
    @UniqueEmail
    private String email;
    @NotBlank
    @PasswordCriteria
    private String password;
    @NotBlank
    @Size(min = 2, max = 100)
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 100)
    private String lastName;
    @NotBlank
    @Size(min = 2, max = 100)
    private String nickName;
    @Min(15)
    @NotNull
    private int age;

    public UserRegistrationDto(Long id, String email, String password, String firstName, String lastName, String nickName, int age) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.age = age;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
