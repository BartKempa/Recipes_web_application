package com.example.recipes.web;

import com.example.recipes.domain.rating.RatingService;
import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class RecipeController {
    private final RecipeService recipeService;
    private final RatingService ratingService;
    private final UserService  userService;

    public RecipeController(RecipeService recipeService, RatingService ratingService, UserService userService) {
        this.recipeService = recipeService;
        this.ratingService = ratingService;
        this.userService = userService;
    }


    @GetMapping("/przepis/{id}")
    public String getRecipe(@PathVariable long id,
                            Model model,
                            Authentication authentication){
        RecipeFullInfoDto recipeFullInfoDto = recipeService.findRecipeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("recipe", recipeFullInfoDto);
        if (authentication != null){
            String currentUserName = authentication.getName();
            Integer rating = ratingService.getRatingForRecipe(currentUserName, id).orElse(0);
            model.addAttribute("userRating", rating);
        }
        int favourites = userService.favoritesCount(id);
        model.addAttribute("favourites", favourites);
        return "recipe";
    }
}
