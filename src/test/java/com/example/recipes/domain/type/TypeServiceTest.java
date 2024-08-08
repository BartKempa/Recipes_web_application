package com.example.recipes.domain.type;

import com.example.recipes.domain.comment.CommentRepository;
import com.example.recipes.domain.rating.RatingRepository;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.type.dto.TypeDto;
import com.example.recipes.domain.user.UserRepository;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class TypeServiceTest {
    private TypeService typeService;
    @Mock private RecipeRepository recipeRepositoryMock;
    @Mock private TypeRepository typeRepositoryMock;
    @Mock private UserRepository userRepositoryMock;
    @Mock private RatingRepository ratingRepositoryMock;
    @Mock private CommentRepository commentRepositoryMock;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
        typeService = new TypeService(typeRepositoryMock, recipeRepositoryMock, ratingRepositoryMock, commentRepositoryMock, userRepositoryMock);

    }

    @Test
    void shouldAddNewType() {
        //given

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