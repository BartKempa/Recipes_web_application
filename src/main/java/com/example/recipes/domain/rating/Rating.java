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
    @ManyToOne()
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
    private Integer rating;

    public Rating() {
    }

    public Rating(User user, Recipe recipe, Integer rating) {
        this.user = user;
        this.recipe = recipe;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
