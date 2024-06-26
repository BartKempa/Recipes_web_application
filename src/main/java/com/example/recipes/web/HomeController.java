package com.example.recipes.web;

import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    private final RecipeService recipeService;
    private final RecipeController recipeController;

    public HomeController(RecipeService recipeService, RecipeController recipeController) {
        this.recipeService = recipeService;
        this.recipeController = recipeController;
    }

    @GetMapping("/")
    public String home(Model model){
        return recipeController.getAllRecipesPageable(Optional.of(1), model);
    }


}
