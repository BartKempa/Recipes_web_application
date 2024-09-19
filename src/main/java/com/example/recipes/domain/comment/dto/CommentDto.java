package com.example.recipes.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

public class CommentDto {
    private Long id;
    private boolean approved;
    @NotBlank
    @Size(min = 2, max = 2000)
    private String text;
    private LocalDateTime creationDate;
    private String userEmail;

    public CommentDto() {
    }

    public CommentDto(Long id, boolean approved, String text, LocalDateTime creationDate, String userEmail) {
        this.id = id;
        this.approved = approved;
        this.text = text;
        this.creationDate = creationDate;
        this.userEmail = userEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentDto that)) return false;
        return approved == that.approved && Objects.equals(id, that.id) && Objects.equals(text, that.text) && Objects.equals(creationDate, that.creationDate) && Objects.equals(userEmail, that.userEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, approved, text, creationDate, userEmail);
    }
}
