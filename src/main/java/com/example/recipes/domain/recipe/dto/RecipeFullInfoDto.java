package com.example.recipes.domain.recipe.dto;

import java.util.List;
import java.util.Objects;

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
    private double averageRating;
    private int ratingCount;
    private int approvedCommentCount;

    public RecipeFullInfoDto(Long id, String name, String type, String description, Integer preparationTime, Integer cookingTime, Integer serving, String difficultyLevel, List<String> ingredients, List<String> directionsSteps, String image, double averageRating, int ratingCount, int approvedCommentCount) {
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
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.approvedCommentCount = approvedCommentCount;
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

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public int getApprovedCommentCount() {
        return approvedCommentCount;
    }

    public void setApprovedCommentCount(int approvedCommentCount) {
        this.approvedCommentCount = approvedCommentCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeFullInfoDto that)) return false;
        return Double.compare(averageRating, that.averageRating) == 0 && ratingCount == that.ratingCount && approvedCommentCount == that.approvedCommentCount && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(type, that.type) && Objects.equals(description, that.description) && Objects.equals(preparationTime, that.preparationTime) && Objects.equals(cookingTime, that.cookingTime) && Objects.equals(serving, that.serving) && Objects.equals(difficultyLevel, that.difficultyLevel) && Objects.equals(ingredients, that.ingredients) && Objects.equals(directionsSteps, that.directionsSteps) && Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, description, preparationTime, cookingTime, serving, difficultyLevel, ingredients, directionsSteps, image, averageRating, ratingCount, approvedCommentCount);
    }
}
