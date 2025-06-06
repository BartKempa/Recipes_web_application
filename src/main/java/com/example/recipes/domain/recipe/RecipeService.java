package com.example.recipes.domain.recipe;

import com.example.recipes.domain.comment.CommentRepository;
import com.example.recipes.domain.difficultyLevel.DifficultyLevelRepository;
import com.example.recipes.domain.rating.RatingRepository;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeSaveDto;
import com.example.recipes.domain.type.TypeRepository;
import com.example.recipes.storage.FileStorageService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final TypeRepository typeRepository;
    private final DifficultyLevelRepository difficultyLevelRepository;
    private final FileStorageService fileStorageService;
    private final RatingRepository ratingRepository;
    private final CommentRepository commentRepository;
    private final RecipeDateTimeProvider recipeDateTimeProvider;

    RecipeService(RecipeRepository recipeRepository, TypeRepository typeRepository, DifficultyLevelRepository difficultyLevelRepository, FileStorageService fileStorageService, RatingRepository ratingRepository, CommentRepository commentRepository, RecipeDateTimeProvider recipeDateTimeProvider) {
        this.recipeRepository = recipeRepository;
        this.typeRepository = typeRepository;
        this.difficultyLevelRepository = difficultyLevelRepository;
        this.fileStorageService = fileStorageService;
        this.ratingRepository = ratingRepository;
        this.commentRepository = commentRepository;
        this.recipeDateTimeProvider = recipeDateTimeProvider;
    }

     public Page<RecipeMainInfoDto> findPaginated(int pageNumber, int pageSize, String sortField){
         Sort sort = getSort(sortField);
         Pageable pageable = getPageable(pageNumber, pageSize, sort);
         return recipeRepository.findAll(pageable)
                 .map(RecipeDtoMapper::mapMainInfo);
     }
    private Pageable getPageable(int pageNumber, int pageSize, Sort sort) {
        return PageRequest.of(pageNumber - 1, pageSize, sort);
    }

    private Sort getSort(String sortField) {
        return Sort.by(sortField).descending();
    }

    public Page<RecipeMainInfoDto> findPaginatedRecipesList(int pageNumber, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending() ;
        Pageable pageable = getPageable(pageNumber, pageSize, sort);
        return recipeRepository.findAll(pageable)
                .map(RecipeDtoMapper::mapMainInfo);
    }

    public Optional<RecipeFullInfoDto> findRecipeById(long id){
        return recipeRepository.findById(id)
                .map(RecipeDtoMapper::mapFullInfo);
    }

    public Optional<RecipeSaveDto> findRecipeToSave(long id){
        return recipeRepository.findById(id)
                .map(RecipeDtoMapper::map);
    }

    public Page<RecipeMainInfoDto> findRecipesByType(String type, int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return recipeRepository.findAllByType_NameIgnoreCase(type, pageable)
               .map(RecipeDtoMapper::mapMainInfo);
    }

    @Transactional
    public void addRecipe(RecipeSaveDto recipeToSave){
        Recipe recipe = new Recipe();
        LocalDateTime date = recipeDateTimeProvider.getCurrentTime();
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
        Sort sort = getSort(sortField);
        Pageable pageable = getPageable(pageNumber, pageSize, sort);
        return recipeRepository.findRecipesBySearchText(searchText, pageable)
                .map(RecipeDtoMapper::mapMainInfo);
    }

    public Page<RecipeMainInfoDto> findFavouriteRecipesForUser(String email, int pageNumber, int pageSize, String sortField){
        Sort sort = getSort(sortField);
        Pageable pageable = getPageable(pageNumber, pageSize, sort);
        return recipeRepository.findAllFavouritesRecipesForUser(email, pageable)
                .map(RecipeDtoMapper::mapMainInfo);
    }

    public long countFavouriteUserRecipes(String email){
        List<Recipe> allFavouritesRecipesForUser = recipeRepository.findAllFavouritesRecipesForUser(email);
        return allFavouritesRecipesForUser.size();
    }

    public Page<RecipeMainInfoDto> findRatedRecipesByUser(String email, int pageNumber, int pageSize, String sortField) {
        Sort sort = getSort(sortField);
        Pageable pageable = getPageable(pageNumber, pageSize, sort);
        return recipeRepository.findAllRatedRecipesByUser(email, pageable)
                .map(RecipeDtoMapper::mapMainInfo);
    }

    public long countRatedRecipeByUser(String email){
        List<Recipe> allRatedRecipesByUser = recipeRepository.findAllRatedRecipesByUser(email);
        return allRatedRecipesByUser.size();
    }

    @Transactional
    public void updateRecipe(RecipeSaveDto recipeToUpdate){
        Recipe recipe = recipeRepository.findById(recipeToUpdate.getId()).orElseThrow();
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

    @Transactional
    public void deleteRecipe(long recipeId){
        Recipe recipeToDelete = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ratingRepository.deleteAll(recipeToDelete.getRatings());
        commentRepository.deleteAll(recipeToDelete.getComments());
        recipeToDelete.getFavourites().forEach(user -> user.getFavoriteRecipes().remove(recipeToDelete));
        recipeToDelete.getFavourites().clear();
        recipeRepository.delete(recipeToDelete);
    }
}
