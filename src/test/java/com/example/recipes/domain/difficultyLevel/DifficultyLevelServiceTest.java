package com.example.recipes.domain.difficultyLevel;

import com.example.recipes.domain.difficultyLevel.dto.DifficultyLevelDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DifficultyLevelServiceTest {
    @Mock private DifficultyLevelRepository difficultyLevelRepositoryMock;
    private DifficultyLevelService difficultyLevelService;

    @BeforeEach
    void init(){
        difficultyLevelService = new DifficultyLevelService(difficultyLevelRepositoryMock);
    }


}