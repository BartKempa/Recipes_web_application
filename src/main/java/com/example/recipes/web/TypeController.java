package com.example.recipes.web;

import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.type.TypeService;
import com.example.recipes.domain.type.dto.TypeDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
public class TypeController {
    private final static int PAGE_SIZE = 3;
    private final RecipeService recipeService;
    private final TypeService typeService;

    public TypeController(RecipeService recipeService, TypeService typeService) {
        this.recipeService = recipeService;
        this.typeService = typeService;
    }

    @GetMapping("/typ/{name}/strona/{pageNo}")
    public String getType(@PathVariable String name,
                          @PathVariable Optional<Integer> pageNo,
                          Model model){
        int pageNumber = pageNo.orElse(1);
        TypeDto typeDto = typeService.findTypeByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<RecipeMainInfoDto> recipePage = recipeService.findRecipesByType(name, pageNumber, PAGE_SIZE);
        List<RecipeMainInfoDto> recipes = recipePage.getContent();
        int totalPages = recipePage.getTotalPages();
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("heading", typeDto.getName());
        model.addAttribute("recipes", recipes);
        model.addAttribute("baseUrl", "/typ/" + name + "/strona");
        return "recipe-listing";
    }
}
