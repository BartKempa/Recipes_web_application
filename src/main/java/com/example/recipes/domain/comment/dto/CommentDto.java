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
    private long recipeId;
    private String userName;
}
