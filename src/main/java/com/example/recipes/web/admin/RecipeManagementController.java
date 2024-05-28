package com.example.recipes.web.admin;

import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeSaveDto;
import com.example.recipes.domain.type.TypeService;
import com.example.recipes.domain.type.dto.TypeDto;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public class RecipeManagementController {
    private final RecipeService recipeService;
    private final TypeService typeService;

    public RecipeManagementController(RecipeService recipeService, TypeService typeService) {
        this.recipeService = recipeService;
        this.typeService = typeService;
    }

    @GetMapping("/admin/dodaj-przepis")
    public String addRecipeForm(Model model){
        RecipeSaveDto recipe = new RecipeSaveDto();
        model.addAttribute("recipe", recipe);
        List<TypeDto> types = typeService.findAllTypes();
        model.addAttribute("types", types);
        return "admin/recipe-form";
    }


}
