package com.example.recipes.web;

import com.example.recipes.domain.comment.CommentService;
import com.example.recipes.domain.comment.dto.CommentDto;
import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
class CommentController {
    private final CommentService commentService;
    private final RecipeService recipeService;
    private final UserService userService;

    CommentController(CommentService commentService, RecipeService recipeService, UserService userService) {
        this.commentService = commentService;
        this.recipeService = recipeService;
        this.userService = userService;
    }

    @PostMapping("/dodaj-komentarz")
    String addRecipeComment(@Valid @ModelAttribute("comment") CommentDto comment,
                                   BindingResult bindingResult,
                                   Authentication authentication,
                                   Model model,
                                   @RequestParam long recipeId){
        if (bindingResult.hasErrors()){
            RecipeFullInfoDto recipeFullInfoDto = recipeService.findRecipeById(recipeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            model.addAttribute("recipe", recipeFullInfoDto);
            model.addAttribute("comments", commentService.getActiveCommentsForRecipe(recipeId));
            model.addAttribute("favourites", userService.favoritesCount(recipeId));
            return "recipe";
        } else {
            String currentUserEmail = authentication.getName();
            commentService.addComment(comment, recipeId, currentUserEmail);
            return "redirect:/przepis/" + recipeId;
        }
    }
}
