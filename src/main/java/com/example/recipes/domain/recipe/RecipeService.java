package com.example.recipes.domain.recipe;

import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeSaveDto;
import com.example.recipes.domain.type.TypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final TypeRepository typeRepository;

    public RecipeService(RecipeRepository recipeRepository, TypeRepository typeRepository) {
        this.recipeRepository = recipeRepository;
        this.typeRepository = typeRepository;
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

    public void addRecipe(RecipeSaveDto recipeToSave){
        Recipe recipe = new Recipe();
        recipe.setId(recipeToSave.getId());
        recipe.setName(recipeToSave.getName());
        recipe.setType(typeRepository.findByNameIgnoreCase(recipeToSave.getName()).orElseThrow());
        recipe.setDescription(recipeToSave.getDescription());
        recipe.setPreparationTime(recipeToSave.getPreparationTime());
        recipe.setCookingTime(recipeToSave.getCookingTime());
        recipe.setServing(recipeToSave.getServing());
        recipe.setDifficultyLevel(recipeToSave.getDifficultyLevel());
        recipe.setIngredients(recipeToSave.getIngredients());
        recipe.setDescription(recipeToSave.getDescription());

    }






}
