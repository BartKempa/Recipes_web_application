package com.example.recipes.domain.rating;

import com.example.recipes.domain.comment.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RatingRepository extends CrudRepository<Rating, Long> {
    Optional<Rating> findByUser_EmailAndRecipe_Id(String userEmail, Long recipeId);
    List<Rating> findAllByUser_Email(String email);



}
