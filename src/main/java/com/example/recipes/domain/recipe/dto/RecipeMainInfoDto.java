package com.example.recipes.domain.recipe.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class RecipeMainInfoDto {
    private Long id;
    private String name;
    private String type;
    private String image;
    private double averageRating;
    private int ratingCount;
    private int approvedCommentCount;
    private LocalDateTime creationDate;

    public RecipeMainInfoDto(Long id, String name, String type, String image, double averageRating, int ratingCount, int approvedCommentCount, LocalDateTime creationDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.image = image;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.approvedCommentCount = approvedCommentCount;
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

    public int getApprovedCommentCount() {
        return approvedCommentCount;
    }

    public void setApprovedCommentCount(int approvedCommentCount) {
        this.approvedCommentCount = approvedCommentCount;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeMainInfoDto that)) return false;
        return Double.compare(averageRating, that.averageRating) == 0 && ratingCount == that.ratingCount && approvedCommentCount == that.approvedCommentCount && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(type, that.type) && Objects.equals(image, that.image) && Objects.equals(creationDate, that.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, image, averageRating, ratingCount, approvedCommentCount, creationDate);
    }
}
