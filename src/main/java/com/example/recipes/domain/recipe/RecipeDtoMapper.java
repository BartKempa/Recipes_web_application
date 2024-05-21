package com.example.recipes.domain.recipe;

import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;

import java.util.Arrays;

class RecipeDtoMapper {
    static RecipeFullInfoDto mapFullInfo(Recipe recipe){
        return new RecipeFullInfoDto(
                recipe.getId(),
                recipe.getName(),
                recipe.getType().getName(),
                recipe.getDescription(),
                recipe.getPreparationTime(),
                recipe.getCookingTime(),
                recipe.getServing(),
                recipe.getDifficultyLevel().getName(),
                recipe.getIngredients(),
                // Zmiana dosłowne \n na rzeczywiste nowe linie
                Arrays.stream(recipe.getDirections().replace("\\n", "\n").split("\\r?\\n")).map(String::trim).toList()
        );
    }

    static RecipeMainInfoDto mapMainInfo(Recipe recipe){
        return new RecipeMainInfoDto(
                recipe.getId(),
                recipe.getName(),
                recipe.getType().getName()
        );
    }








}
