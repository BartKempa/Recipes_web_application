package com.example.recipes.domain.recipe.dto;

import java.time.LocalDateTime;

public class RecipeMainInfoDto {
    private Long id;
    private String name;
    private String type;
    private String image;
    private double averageRating;
    private int ratingCount;
    private int commentCount;
    private LocalDateTime creationDate;

    public RecipeMainInfoDto(Long id, String name, String type, String image, double averageRating, int ratingCount, int commentCount, LocalDateTime creationDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.image = image;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.commentCount = commentCount;
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

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
