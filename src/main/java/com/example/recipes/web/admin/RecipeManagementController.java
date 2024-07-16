package com.example.recipes.web.admin;

import com.example.recipes.domain.difficultyLevel.DifficultyLevelService;
import com.example.recipes.domain.difficultyLevel.dto.DifficultyLevelDto;
import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeSaveDto;
import com.example.recipes.domain.type.TypeService;
import com.example.recipes.domain.type.dto.TypeDto;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class RecipeManagementController {
    private final RecipeService recipeService;
    private final TypeService typeService;
    private final DifficultyLevelService difficultyLevelService;

    public RecipeManagementController(RecipeService recipeService, TypeService typeService, DifficultyLevelService difficultyLevelService) {
        this.recipeService = recipeService;
        this.typeService = typeService;
        this.difficultyLevelService = difficultyLevelService;
    }

    @GetMapping("/admin/dodaj-przepis")
    public String addRecipeForm(Model model){
        RecipeSaveDto recipe = new RecipeSaveDto();
        model.addAttribute("recipe", recipe);
        List<TypeDto> types = typeService.findAllTypes();
        model.addAttribute("types", types);
        List<DifficultyLevelDto> allDifficultyLevelDto = difficultyLevelService.findAllDifficultyLevelDto();
        model.addAttribute("allDifficultyLevelDto", allDifficultyLevelDto);
        return "admin/recipe-form";
    }

    @PostMapping("/admin/dodaj-przepis")
    public String addRecipe(RecipeSaveDto recipe, RedirectAttributes redirectAttributes){
        recipeService.addRecipe(recipe);
        redirectAttributes.addFlashAttribute(
                AdminController.ADMIN_NOTIFICATION_ATTRIBUTE,
                "Przepis %s został zapisany".formatted(recipe.getName()));
        return "redirect:/admin";
    }

    @GetMapping("/admin/aktualizuj-przepis/{recipeId}")
    public String updateRecipe(@PathVariable long recipeId,
                             Model model){
        RecipeFullInfoDto recipe = recipeService.findRecipeById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("recipe", recipe);
        return "recipe-update-form";
    }
}
