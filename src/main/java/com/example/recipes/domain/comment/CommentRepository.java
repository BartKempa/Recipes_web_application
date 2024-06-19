package com.example.recipes.domain.comment;

import com.example.recipes.domain.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findAllByRecipeId(long recipeId);

}
