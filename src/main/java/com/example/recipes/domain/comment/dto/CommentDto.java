package com.example.recipes.domain.comment.dto;

import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.user.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

public class CommentDto {
    private Long id;
    private boolean approved;
    private String text;
    private LocalDateTime creationDate;

    public CommentDto(Long id, boolean approved, String text, LocalDateTime creationDate) {
        this.id = id;
        this.approved = approved;
        this.text = text;
        this.creationDate = creationDate;
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
}
