package com.example.recipes.domain.difficultyLevel;

import com.example.recipes.domain.difficultyLevel.dto.DifficultyLevelDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DifficultyLevelServiceTest {
    @Mock
    private DifficultyLevelRepository difficultyLevelRepositoryMock;
    private DifficultyLevelService difficultyLevelService;

    @BeforeEach
    void init() {
        difficultyLevelService = new DifficultyLevelService(difficultyLevelRepositoryMock);
    }

    @Test
    void shouldFindAllDifficultyLevelDto() {
        //given
        DifficultyLevel difficultyLevel1 = new DifficultyLevel();
        difficultyLevel1.setId(1L);
        difficultyLevel1.setName("proste");

        DifficultyLevel difficultyLevel2 = new DifficultyLevel();
        difficultyLevel2.setId(2L);
        difficultyLevel2.setName("trudne");

        List<DifficultyLevel> difficultyLevelList = Arrays.asList(difficultyLevel1, difficultyLevel2);

        Mockito.when(difficultyLevelRepositoryMock.findAll()).thenReturn(difficultyLevelList);

        //when
        List<DifficultyLevelDto> allDifficultyLevelDto = difficultyLevelService.findAllDifficultyLevelDto();

        //then
        assertThat(allDifficultyLevelDto.size(), is(2));
        assertThat(allDifficultyLevelDto.get(0).getName(), is("proste"));
        assertThat(allDifficultyLevelDto.get(1).getId(), is(2L));
    }

    @Test
    void shouldReturnEmptyListWhenNoDifficultyLevelsFound() {
        //given
        Mockito.when(difficultyLevelRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        //when
        List<DifficultyLevelDto> allDifficultyLevelDto = difficultyLevelService.findAllDifficultyLevelDto();

        //then
        assertThat(allDifficultyLevelDto.isEmpty(), is(true));
    }

    @Test
    void shouldThrowExceptionWhenRepositoryFails() {
        //given
        Mockito.when(difficultyLevelRepositoryMock.findAll()).thenThrow(new RuntimeException("Database error"));

        //when & then
        RuntimeException exc = assertThrows(RuntimeException.class, () -> difficultyLevelService.findAllDifficultyLevelDto());
        assertThat(exc.getMessage(), is(HttpStatus.NOT_FOUND));
    }
}