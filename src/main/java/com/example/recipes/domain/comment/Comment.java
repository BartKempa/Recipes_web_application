package com.example.recipes.domain.comment;

import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime creationDate;
    private boolean approved;
    private String text;
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
