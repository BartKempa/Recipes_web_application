package com.example.recipes.domain.recipe.dto;

public class RecipeMainInfoDto {
    private Long id;
    private String name;
    private String type;
    private String image;

    public RecipeMainInfoDto(Long id, String name, String type, String photo) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.image = photo;
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
}
