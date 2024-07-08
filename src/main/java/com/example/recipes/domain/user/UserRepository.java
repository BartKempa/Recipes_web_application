package com.example.recipes.domain.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT (u) FROM User u JOIN u.favoriteRecipes r WHERE r.id = :recipeId")
    int countUsersByFavouriteRecipe(long recipeId);

    @Modifying
    @Query("DELETE FROM User u WHERE u.email = :email")
    void deleteUsers(String email);

}
