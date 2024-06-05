package com.example.recipes.domain.rating;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RatingRepository extends CrudRepository<Rating, Long> {
    Optional<Rating> findByUser_EmailAndRecipe_Id(String userEmail, Long recipeId);
}
