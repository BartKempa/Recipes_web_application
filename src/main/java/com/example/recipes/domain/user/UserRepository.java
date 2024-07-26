package com.example.recipes.domain.user;

import com.example.recipes.domain.recipe.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long>, PagingAndSortingRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT (u) FROM User u JOIN u.favoriteRecipes r WHERE r.id = :recipeId")
    int countUsersByFavouriteRecipe(long recipeId);


}
