package com.example.recipes.domain.recipe.dto;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class RecipeSaveDto {
    private Long id;
    private String name;
    private String type;
    private String description;
    private Integer preparationTime;
    private Integer cookingTime;
    private Integer serving;
    private String difficultyLevel;
    private String ingredients;
    private String directionsSteps;
    private MultipartFile image;
    private LocalDateTime creationDate;

    public RecipeSaveDto() {
    }

    public RecipeSaveDto(Long id, String name, String type, String description, Integer preparationTime, Integer cookingTime, Integer serving, String difficultyLevel, String ingredients, String directionsSteps, MultipartFile image, LocalDateTime creationDate) {
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
        this.creationDate = creationDate;
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

    public String getDirectionsSteps() {
        return directionsSteps;
    }

    public void setDirectionsSteps(String directionsSteps) {
        this.directionsSteps = directionsSteps;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
