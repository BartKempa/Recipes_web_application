package com.example.recipes.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
class HomeController {
    private final RecipeController recipeController;

    HomeController(RecipeController recipeController) {
        this.recipeController = recipeController;
    }

    @GetMapping("/")
    String home(Model model){
        model.addAttribute("baseUrl", "/strona");
        return recipeController.getAllRecipesPageable(Optional.of(1),"dataPublikacji", model);
    }
}
