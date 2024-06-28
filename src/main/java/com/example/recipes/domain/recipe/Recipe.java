package com.example.recipes.domain.recipe;

import com.example.recipes.domain.comment.Comment;
import com.example.recipes.domain.difficultyLevel.DifficultyLevel;
import com.example.recipes.domain.rating.Rating;
import com.example.recipes.domain.type.Type;
import com.example.recipes.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private Type type;

    @OneToMany(mappedBy = "recipe")
    private Set<Rating> ratings = new HashSet<>();
    private String description;
    private Integer preparationTime;
    private Integer cookingTime;
    private Integer serving;
    @ManyToOne
    @JoinColumn(name = "difficultyLevel_id", referencedColumnName = "id")
    private DifficultyLevel difficultyLevel;
    private String ingredients;
    private String directions;
    private String image;
    @OneToMany(mappedBy = "recipe")
    private Set<Comment> comments = new HashSet<>();
    private LocalDateTime creationDate;
    @ManyToMany(mappedBy = "favoriteRecipes")
    private Set<User> favourites = new HashSet<>();


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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
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

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
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

    public String getImage() {
        return image;
    }

    public void setImage(String photo) {
        this.image = photo;
    }

    public Set<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(Set<Rating> ratings) {
        this.ratings = ratings;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Set<User> getFavourites() {
        return favourites;
    }

    public void setFavourites(Set<User> favourites) {
        this.favourites = favourites;
    }
}
