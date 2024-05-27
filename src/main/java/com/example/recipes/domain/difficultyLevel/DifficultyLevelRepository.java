package com.example.recipes.domain.difficultyLevel;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface DifficultyLevelRepository extends CrudRepository<DifficultyLevel, Long> {
    Optional<DifficultyLevel> findByName(String difficultyLevel);
}
