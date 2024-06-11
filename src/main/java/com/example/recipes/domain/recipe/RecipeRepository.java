package com.example.recipes.domain.recipe;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    List<Recipe> findAllByType_NameIgnoreCase(String name);



}
