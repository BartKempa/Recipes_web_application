package com.example.recipes.web;

import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.type.TypeService;
import com.example.recipes.domain.type.dto.TypeDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class TypeController {
    private final RecipeService recipeService;
    private final TypeService typeService;

    public TypeController(RecipeService recipeService, TypeService typeService) {
        this.recipeService = recipeService;
        this.typeService = typeService;
    }

    @GetMapping("/przepisy/{name}")
    public String getType(@PathVariable String name, Model model){
        TypeDto typeDto = typeService.findTypeByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<RecipeMainInfoDto> recipes = recipeService.findRecipesByType(name);
        model.addAttribute("heading", typeDto.getName());
        model.addAttribute("recipes", recipes);
        return "recipe-listing";
    }
}
