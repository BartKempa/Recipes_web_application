package com.example.recipes.domain.difficultyLevel;

import com.example.recipes.domain.difficultyLevel.dto.DifficultyLevelDto;

class DifficultyLevelMapper {
    static DifficultyLevelDto map(DifficultyLevel difficultyLevel){
        return new DifficultyLevelDto(
                difficultyLevel.getId(),
                difficultyLevel.getName()
        );
    }
}
