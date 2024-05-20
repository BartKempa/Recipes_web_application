package com.example.recipes.domain.recipe.dto;

import com.example.recipes.domain.difficultyLevel.DifficultyLevel;
import com.example.recipes.domain.type.Type;
import jakarta.persistence.*;

public class RecipeDto {
    private Long id;
    private String name;
    private String type;
    private String description;
    private Integer preparationTime;
    private Integer cookingTime;
    private Integer serving;
    private String difficultyLevel;
    private String ingredients;
    private String directions;

    public RecipeDto(Long id, String name, String type, String description, Integer preparationTime, Integer cookingTime, Integer serving, String difficultyLevel, String ingredients, String directions) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.serving = serving;
        this.difficultyLevel = difficultyLevel;
        this.ingredients = ingredients;
        this.directions = directions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    public Integer getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(Integer cookingTime) {
        this.cookingTime = cookingTime;
    }

    public Integer getServing() {
        return serving;
    }

    public void setServing(Integer serving) {
        this.serving = serving;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }
}
