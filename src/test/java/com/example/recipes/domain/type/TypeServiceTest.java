package com.example.recipes.domain.type;

import com.example.recipes.domain.comment.CommentRepository;
import com.example.recipes.domain.rating.RatingRepository;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.type.dto.TypeDto;
import com.example.recipes.domain.user.UserRepository;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
    void shouldFindAllTypes() {
        //given
        RecipeRepository recipeRepositoryMock = Mockito.mock(RecipeRepository.class);
        TypeRepository typeRepositoryMock = Mockito.mock(TypeRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        RatingRepository ratingRepositoryMock = Mockito.mock(RatingRepository.class);
        CommentRepository commentRepositoryMock = Mockito.mock(CommentRepository.class);
        TypeService typeService = new TypeService(typeRepositoryMock, recipeRepositoryMock, ratingRepositoryMock, commentRepositoryMock, userRepository);

        Type type1 = new Type();
        type1.setId(1L);
        type1.setName("Zupa");

        Type type2 = new Type();
        type2.setId(2L);
        type2.setName("Pizza");

        List<Type> typeList = new ArrayList<>();
        typeList.add(type1);
        typeList.add(type2);

        Mockito.when(typeRepositoryMock.findAll()).thenReturn(typeList);

        //when
        List<TypeDto> allTypes = typeService.findAllTypes();

        //then
        MatcherAssert.assertThat(allTypes, hasSize(2));
        assertThat(allTypes.get(0).getName(), is("Zupa"));
        assertThat(allTypes.get(1).getName(), is("Pizza"));
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