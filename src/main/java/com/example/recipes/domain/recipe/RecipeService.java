package com.example.recipes.domain.recipe;

import com.example.recipes.domain.difficultyLevel.DifficultyLevelRepository;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeSaveDto;
import com.example.recipes.domain.type.TypeRepository;
import com.example.recipes.storage.FileStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final TypeRepository typeRepository;
    private final DifficultyLevelRepository difficultyLevelRepository;
    private final FileStorageService fileStorageService;

    public RecipeService(RecipeRepository recipeRepository, TypeRepository typeRepository, DifficultyLevelRepository difficultyLevelRepository, FileStorageService fileStorageService) {
        this.recipeRepository = recipeRepository;
        this.typeRepository = typeRepository;
        this.difficultyLevelRepository = difficultyLevelRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<RecipeMainInfoDto> findAllRecipes() {
        return StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .map(RecipeDtoMapper::mapMainInfo)
                .toList();
    }
     public Page<RecipeMainInfoDto> findPaginated(int pageNumber, int pageSize){
         Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
         return this.recipeRepository.findAll(pageable)
                 .map(RecipeDtoMapper::mapMainInfo);
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

    @Transactional
    public void addRecipe(RecipeSaveDto recipeToSave){
        Recipe recipe = new Recipe();
        recipe.setId(recipeToSave.getId());
        recipe.setName(recipeToSave.getName());
                recipe.setType(typeRepository.findByNameIgnoreCase(recipeToSave.getType()).orElseThrow());
        recipe.setDescription(recipeToSave.getDescription());
        recipe.setPreparationTime(recipeToSave.getPreparationTime());
        recipe.setCookingTime(recipeToSave.getCookingTime());
        recipe.setServing(recipeToSave.getServing());
        recipe.setDifficultyLevel(difficultyLevelRepository.findByName(recipeToSave.getDifficultyLevel()).orElseThrow());
        recipe.setIngredients(recipeToSave.getIngredients());
        recipe.setDirections(recipeToSave.getDirectionsSteps());
        if (recipeToSave.getImage() != null && !recipeToSave.getImage().isEmpty()) {
            String savedImage = fileStorageService.saveImage(recipeToSave.getImage());
            recipe.setImage(savedImage);
        }
        recipeRepository.save(recipe);
    }
}
