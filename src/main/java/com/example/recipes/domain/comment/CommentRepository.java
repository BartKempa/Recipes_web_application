package com.example.recipes.domain.comment;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findAllByRecipeId(long recipeId);
}
