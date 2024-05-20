package com.example.recipes.domain.recipe;

import com.example.recipes.domain.recipe.dto.RecipeDto;

class RecipeDtoMapper {

    static RecipeDto map(Recipe recipe){
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
                recipe.getDirections()
        );
    }


}
