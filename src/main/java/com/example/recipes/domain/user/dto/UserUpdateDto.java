package com.example.recipes.domain.user.dto;

import com.example.recipes.domain.user.validation.PasswordCriteria;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserUpdateDto {
    private Long id;
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

    public UserUpdateDto(Long id, String password, String firstName, String lastName, String nickName, int age) {
        this.id = id;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.age = age;
    }

    public UserUpdateDto() {
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public int getAge() {
        return age;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
