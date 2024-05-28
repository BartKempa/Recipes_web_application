package com.example.recipes.domain.recipe.dto;

import java.util.List;

public class RecipeFullInfoDto {
    private Long id;
    private String name;
    private String type;
    private String description;
    private Integer preparationTime;
    private Integer cookingTime;
    private Integer serving;
    private String difficultyLevel;
    private List<String> ingredients;
    private List<String> directionsSteps;
    private String image;

    public RecipeFullInfoDto(Long id, String name, String type, String description, Integer preparationTime, Integer cookingTime, Integer serving, String difficultyLevel, List<String> ingredients, List<String> directionsSteps, String image) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.serving = serving;
        this.difficultyLevel = difficultyLevel;
        this.ingredients = ingredients;
        this.directionsSteps = directionsSteps;
        this.image = image;
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

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getDirectionsSteps() {
        return directionsSteps;
    }

    public void setDirectionsSteps(List<String> directionsSteps) {
        this.directionsSteps = directionsSteps;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
