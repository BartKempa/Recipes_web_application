package com.example.recipes.domain.recipe;

import com.example.recipes.domain.rating.Rating;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;

import java.util.Arrays;
import java.util.stream.Collectors;

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
                Arrays.stream(recipe.getIngredients().split("\\\\n")).map(String::trim).collect(Collectors.toList()),
                //Arrays.stream(recipe.getIngredients().replace("\\n", "\n").split("\\n?\\r")).map(String::trim).collect(Collectors.toList()),
                // Zmiana dosłowne \n na rzeczywiste nowe liniespli replace.("\\n", "\n").split("\\n?\\r")
                //Arrays.stream(recipe.getDirections().replace("\\n", "\n").split("\\n?\\r")).map(String::trim).collect(Collectors.toList()),
                Arrays.stream(recipe.getDirections().split("\\\\n")).map(String::trim).collect(Collectors.toList()),
                recipe.getImage(),
                getAverageRating(recipe),
                recipe.getRatings().size()
        );
    }

    private static double getAverageRating(Recipe recipe) {
        return recipe.getRatings().stream()
                .map(Rating::getRating)
                .mapToDouble(d -> d)
                .average().orElse(0);
    }

    static RecipeMainInfoDto mapMainInfo(Recipe recipe){
        return new RecipeMainInfoDto(
                recipe.getId(),
                recipe.getName(),
                recipe.getType().getName(),
                recipe.getImage(),
                getAverageRating(recipe)
        );
    }

}
