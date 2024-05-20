package com.example.recipes.web;

import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipe/{id}")
    public String getRecipe(@PathVariable long id, Model model){
        Optional<RecipeDto> optionalRecipeDto = recipeService.findRecipeById(id);
        optionalRecipeDto.ifPresent(recipe -> model.addAttribute("recipe", recipe));
        return "recipe";
    }
}
