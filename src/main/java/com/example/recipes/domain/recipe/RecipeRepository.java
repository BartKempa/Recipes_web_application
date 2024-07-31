package com.example.recipes.domain.recipe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends CrudRepository<Recipe, Long>, PagingAndSortingRepository<Recipe, Long> {
    Page<Recipe> findAllByType_NameIgnoreCase(String name, Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE " +
            "LOWER(r.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(r.ingredients) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(r.directions) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<Recipe> findRecipesBySearchText(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT r FROM Recipe r JOIN r.favourites f WHERE f.email = :email")
    Page<Recipe> findAllFavouritesRecipesForUser(@Param("email") String email, Pageable pageable);

    @Query("SELECT r FROM Recipe r JOIN r.favourites f WHERE f.email = :email")
    List<Recipe> findAllFavouritesRecipesForUser(@Param("email") String email);

    @Query("SELECT r FROM Recipe r JOIN r.ratings rt WHERE rt.user.email = :email")
    Page<Recipe> findAllRatedRecipesByUser(@Param("email") String email, Pageable pageable);


    void deleteAllByType_Id(long id);

    List<Recipe> findAllByType_Id(long id);
}
