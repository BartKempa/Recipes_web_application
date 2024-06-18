package com.example.recipes.domain.comment;

import com.example.recipes.domain.comment.dto.CommentDto;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, RecipeRepository recipeRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    public List<CommentDto> getCommentsForRecipe(long id){
         return commentRepository.findAllByRecipeId(id).stream()
                 .map(CommentDtoMapper::map)
                 .collect(Collectors.toList());

    }


}
