package com.example.recipes.domain.difficultyLevel;

import com.example.recipes.domain.difficultyLevel.dto.DifficultyLevelDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DifficultyLevelService {
    private final DifficultyLevelRepository difficultyLevelRepository;

    public DifficultyLevelService(DifficultyLevelRepository difficultyLevelRepository) {
        this.difficultyLevelRepository = difficultyLevelRepository;
    }

    public Optional<DifficultyLevelDto> findDifficultyLevelByName(String name){
        return difficultyLevelRepository.findByName(name).
                map(DifficultyLevelMapper::map);
    }
}
