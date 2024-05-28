package com.example.recipes.domain.difficultyLevel;

import com.example.recipes.domain.difficultyLevel.dto.DifficultyLevelDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

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

    public List<DifficultyLevelDto> findAllDifficultyLevelDto(){
        return StreamSupport.stream(difficultyLevelRepository.findAll().spliterator(),false)
                .map(DifficultyLevelMapper::map)
                .toList();
    }
}
