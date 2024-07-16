package com.example.recipes.domain.recipe;

import com.example.recipes.domain.difficultyLevel.DifficultyLevelRepository;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeSaveDto;
import com.example.recipes.domain.type.TypeRepository;
import com.example.recipes.storage.FileStorageService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

     public Page<RecipeMainInfoDto> findPaginated(int pageNumber, int pageSize, String sortField){
         Sort sort = Sort.by(sortField).descending();
         Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
         return this.recipeRepository.findAll(pageable)
                 .map(RecipeDtoMapper::mapMainInfo);
     }

    public Optional<RecipeFullInfoDto> findRecipeById(long id){
        return recipeRepository.findById(id)
                .map(RecipeDtoMapper::mapFullInfo);
    }

    public Page<RecipeMainInfoDto> findRecipesByType(String type, int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return recipeRepository.findAllByType_NameIgnoreCase(type, pageable)
               .map(RecipeDtoMapper::mapMainInfo);
    }

    @Transactional
    public void addRecipe(RecipeSaveDto recipeToSave){
        Recipe recipe = new Recipe();
        LocalDateTime date = LocalDateTime.now();
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
        recipe.setCreationDate(date);
        recipeRepository.save(recipe);
    }

    public Page<RecipeMainInfoDto> findRecipesByText(String searchText, int pageNumber, int pageSize, String sortField){
        Sort sort = Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        return recipeRepository.findRecipesBySearchText(searchText, pageable)
                .map(RecipeDtoMapper::mapMainInfo);
    }

    public Page<RecipeMainInfoDto> findFavouriteRecipesForUser(String email, int pageNumber, int pageSize, String sortField){
        Sort sort = Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        return recipeRepository.findAllFavouritesRecipesForUser(email, pageable)
                .map(RecipeDtoMapper::mapMainInfo);
    }

    public Page<RecipeMainInfoDto> findRatedRecipesForUser(String email, int pageNumber, int pageSize, String sortField) {
        Sort sort = Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        return recipeRepository.findAllRatedRecipesByUser(email, pageable)
                .map(RecipeDtoMapper::mapMainInfo);
    }

    @Transactional
    public void updateRecipe(RecipeSaveDto recipeToUpdate){
        Recipe recipe = recipeRepository.findById(recipeToUpdate.getId()).orElseThrow();
        recipe.setId(recipeToUpdate.getId());
        recipe.setName(recipeToUpdate.getName());
        recipe.setType(typeRepository.findByNameIgnoreCase(recipeToUpdate.getType()).orElseThrow());
        recipe.setDescription(recipeToUpdate.getDescription());
        recipe.setPreparationTime(recipeToUpdate.getPreparationTime());
        recipe.setCookingTime(recipeToUpdate.getCookingTime());
        recipe.setServing(recipeToUpdate.getServing());
        recipe.setDifficultyLevel(difficultyLevelRepository.findByName(recipeToUpdate.getDifficultyLevel()).orElseThrow());
        recipe.setIngredients(recipeToUpdate.getIngredients());
        recipe.setDirections(recipeToUpdate.getDirectionsSteps());
        if (recipeToUpdate.getImage() != null && !recipeToUpdate.getImage().isEmpty()) {
            String savedImage = fileStorageService.saveImage(recipeToUpdate.getImage());
            recipe.setImage(savedImage);
        }
        recipe.setCreationDate(recipeToUpdate.getCreationDate());
        recipeRepository.save(recipe);
    }
}
