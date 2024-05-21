package com.example.recipes.web;

import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final RecipeService recipeService;

    public HomeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/")
    public String home(Model model){
        List<RecipeMainInfoDto> recipes = recipeService.findAllRecipes();
        model.addAttribute("heading", "Wszytskie przepisy");
        model.addAttribute("recipes", recipes);
        return "recipe-listing";
    }
}
