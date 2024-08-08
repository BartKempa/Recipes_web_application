package com.example.recipes.domain.type;

import com.example.recipes.domain.comment.CommentRepository;
import com.example.recipes.domain.rating.RatingRepository;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.type.dto.TypeDto;
import com.example.recipes.domain.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TypeServiceTest {

    @Test
    void shouldAddNewType() {
        //given
        RecipeRepository recipeRepositoryMock = Mockito.mock(RecipeRepository.class);
        TypeRepository typeRepositoryMock = Mockito.mock(TypeRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        RatingRepository ratingRepositoryMock = Mockito.mock(RatingRepository.class);
        CommentRepository commentRepositoryMock = Mockito.mock(CommentRepository.class);
        TypeService typeService = new TypeService(typeRepositoryMock, recipeRepositoryMock, ratingRepositoryMock, commentRepositoryMock, userRepository);

        //when
        TypeDto typeDto = new TypeDto(null, "Zupy" );
        typeService.addType(typeDto);

        //then
        ArgumentCaptor<Type> typeArgumentCaptor = ArgumentCaptor.forClass(Type.class);
        Mockito.verify(typeRepositoryMock).save(typeArgumentCaptor.capture());

        Type savedType = typeArgumentCaptor.getValue();
        assertEquals("Zupy", savedType.getName());
    }

    @Test
    void shouldFindTypeByNameIgnoreCase() {
        //given
        RecipeRepository recipeRepositoryMock = Mockito.mock(RecipeRepository.class);
        TypeRepository typeRepositoryMock = Mockito.mock(TypeRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        RatingRepository ratingRepositoryMock = Mockito.mock(RatingRepository.class);
        CommentRepository commentRepositoryMock = Mockito.mock(CommentRepository.class);
        TypeService typeService = new TypeService(typeRepositoryMock, recipeRepositoryMock, ratingRepositoryMock, commentRepositoryMock, userRepository);

        Type type = new Type();
        type.setId(1L);
        type.setName("Zupa");

        Mockito.when(typeRepositoryMock.findByNameIgnoreCase("ZUPA")).thenReturn(Optional.of(type));

        //when
        Optional<TypeDto> result = typeService.findTypeByName("ZUPA" );

        //then
        assertTrue(result.isPresent());
        assertEquals("Zupa", result.get().getName());
    }

    @Test
    void findAllTypes() {
    }

    @Test
    void findPaginatedTypesList() {
    }

    @Test
    void deleteType() {
    }

    @Test
    void updateType() {
    }

    @Test
    void findTypeById() {
    }
}