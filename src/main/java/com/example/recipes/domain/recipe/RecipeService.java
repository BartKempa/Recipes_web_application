package com.example.recipes.domain.recipe;

import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<RecipeMainInfoDto> findAllRecipes() {
        return StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .map(RecipeDtoMapper::mapMainInfo)
                .toList();
    }

    public Optional<RecipeFullInfoDto> findRecipeById(long id){
        return recipeRepository.findById(id)
                .map(RecipeDtoMapper::mapFullInfo);
    }

    public List<RecipeMainInfoDto> findRecipesByType(String type){
       return recipeRepository.findAllByType_NameIgnoreCase(type).stream()
               .map(RecipeDtoMapper::mapMainInfo)
               .toList();
    }




}
