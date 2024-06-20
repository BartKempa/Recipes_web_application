package com.example.recipes.domain.comment;

import com.example.recipes.domain.comment.dto.CommentDto;
import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Transactional
    public void addComment(CommentDto commentDto, long recipeId, String userName){
        Comment commentToSave = new Comment();
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
        User user = userRepository.findByEmail(userName).orElseThrow();
        commentToSave.setId(commentDto.getId());
        commentToSave.setCreationDate(LocalDateTime.now());
        commentToSave.setApproved(true);
        commentToSave.setText(commentDto.getText());
        commentToSave.setRecipe(recipe);
        commentToSave.setUser(user);
        commentRepository.save(commentToSave);
    }
}
