package com.example.recipes.web.admin;

import com.example.recipes.domain.difficultyLevel.DifficultyLevelService;
import com.example.recipes.domain.difficultyLevel.dto.DifficultyLevelDto;
import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeSaveDto;
import com.example.recipes.domain.type.TypeService;
import com.example.recipes.domain.type.dto.TypeDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class RecipeManagementController {
    private final static int PAGE_SIZE = 6;
    private final RecipeService recipeService;
    private final TypeService typeService;
    private final DifficultyLevelService difficultyLevelService;

    public RecipeManagementController(RecipeService recipeService, TypeService typeService, DifficultyLevelService difficultyLevelService) {
        this.recipeService = recipeService;
        this.typeService = typeService;
        this.difficultyLevelService = difficultyLevelService;
    }

    private static final Map<String, String> SORT_FIELD_MAP = new HashMap<>();
    static {
        SORT_FIELD_MAP.put("dataDodania", "creationDate");
        SORT_FIELD_MAP.put("nazwa", "name");
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
    public String addRecipe(@Valid @ModelAttribute("recipe") RecipeSaveDto recipe,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()){
            model.addAttribute("recipe", recipe);
            model.addAttribute("types", typeService.findAllTypes());
            model.addAttribute("allDifficultyLevelDto", difficultyLevelService.findAllDifficultyLevelDto());
            return "admin/recipe-form";
        } else {
            recipeService.addRecipe(recipe);
            redirectAttributes.addFlashAttribute(
                    AdminController.ADMIN_NOTIFICATION_ATTRIBUTE,
                    "Przepis %s został zapisany".formatted(recipe.getName()));
            return "redirect:/admin";
        }
    }

    @GetMapping("/admin/lista-przepisow/{pageNo}")
    public String getRecipesList(@PathVariable Optional<Integer> pageNo,
                                 @RequestParam(value = "poleSortowania", required = false) String poleSortowania,
                                 @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                                 Model model){
        int pageNumber = pageNo.orElse(1);
        String sortField = SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
        Page<RecipeMainInfoDto> recipePage = recipeService.findPaginatedRecipesList(pageNumber, PAGE_SIZE, sortField, sortDir);
        List<RecipeMainInfoDto> recipes = recipePage.getContent();
        int totalPages = recipePage.getTotalPages();
        model.addAttribute("recipes", recipes);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("heading", "Lista przepisów");
        model.addAttribute("sortField", poleSortowania);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("baseUrl", "/admin/lista-przepisow");
        return "admin/admin-recipe-list";
    }

    @GetMapping("/admin/aktualizuj-przepis/{recipeId}")
    public String updateRecipeForm(@PathVariable long recipeId,
                                   Model model){
        RecipeSaveDto recipe = recipeService.findRecipeToSave(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<TypeDto> types = typeService.findAllTypes();
        List<DifficultyLevelDto> allDifficultyLevelDto = difficultyLevelService.findAllDifficultyLevelDto();
        model.addAttribute("allDifficultyLevelDto", allDifficultyLevelDto);
        model.addAttribute("types", types);
        model.addAttribute("recipe", recipe);
        return "admin/recipe-update-form";
    }

    @PostMapping("/admin/aktualizuj-przepis")
    public String updateRecipe(@Valid @ModelAttribute("recipe") RecipeSaveDto recipe,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()){
            model.addAttribute("recipe", recipe);
            model.addAttribute("types", typeService.findAllTypes());
            model.addAttribute("allDifficultyLevelDto", difficultyLevelService.findAllDifficultyLevelDto());
            return "admin/recipe-update-form";
        } else {
            recipeService.updateRecipe(recipe);
            redirectAttributes.addFlashAttribute(AdminController.ADMIN_NOTIFICATION_ATTRIBUTE,
                    "Przepis %s został pomyślnie zaktualizowany".formatted(recipe.getName()));
            return "redirect:/admin";
        }
    }
    
    @PostMapping("/admin/usun-przepis")
    public String deleteRecipe(@RequestParam(value = "id") Long id,
                               @RequestHeader String referer){
        recipeService.deleteRecipe(id);
        return "redirect:" + referer;
    }
}
