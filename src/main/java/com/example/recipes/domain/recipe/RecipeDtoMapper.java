package com.example.recipes.domain.recipe;

import com.example.recipes.domain.recipe.dto.RecipeDto;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class RecipeDtoMapper {

    static RecipeDto map(Recipe recipe){
        // Pobierz directions z receptury
        String directions = recipe.getDirections();
        // Zamień dosłowne \n na rzeczywiste nowe linie
        directions = directions.replace("\\n", "\n");

        // Podziel directions na kroki
        List<String> steps = Arrays.stream(directions.split("\\r?\\n")).map(String::trim).toList();

        return new RecipeDto(
                recipe.getId(),
                recipe.getName(),
                recipe.getType().getName(),
                recipe.getDescription(),
                recipe.getPreparationTime(),
                recipe.getCookingTime(),
                recipe.getServing(),
                recipe.getDifficultyLevel().getName(),
                recipe.getIngredients(),
                steps
        );
    }


}
