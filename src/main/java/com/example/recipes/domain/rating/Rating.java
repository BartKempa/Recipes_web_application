package com.example.recipes.domain.rating;

import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "recipe_rating")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

}
