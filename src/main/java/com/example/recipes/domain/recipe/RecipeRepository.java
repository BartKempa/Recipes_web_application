package com.example.recipes.domain.recipe;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends CrudRepository<Recipe, Long>, PagingAndSortingRepository<Recipe, Long> {
    List<Recipe> findAllByType_NameIgnoreCase(String name);



}
