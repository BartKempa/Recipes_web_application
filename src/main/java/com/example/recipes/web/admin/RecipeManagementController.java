package com.example.recipes.web.admin;

import com.example.recipes.domain.recipe.RecipeService;

public class RecipeManagementController {
    private final RecipeService recipeService;

    public RecipeManagementController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
}
