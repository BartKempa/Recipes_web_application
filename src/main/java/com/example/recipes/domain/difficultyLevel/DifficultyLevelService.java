package com.example.recipes.domain.difficultyLevel;

import com.example.recipes.domain.difficultyLevel.dto.DifficultyLevelDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class DifficultyLevelService {
    private final DifficultyLevelRepository difficultyLevelRepository;

    DifficultyLevelService(DifficultyLevelRepository difficultyLevelRepository) {
        this.difficultyLevelRepository = difficultyLevelRepository;
    }

    public List<DifficultyLevelDto> findAllDifficultyLevelDto(){
        return StreamSupport.stream(difficultyLevelRepository.findAll().spliterator(),false)
                .map(DifficultyLevelMapper::map)
                .toList();
    }
}
