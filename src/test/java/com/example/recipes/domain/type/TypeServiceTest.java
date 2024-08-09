package com.example.recipes.domain.type;

import com.example.recipes.domain.comment.Comment;
import com.example.recipes.domain.comment.CommentRepository;
import com.example.recipes.domain.rating.Rating;
import com.example.recipes.domain.rating.RatingRepository;
import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.type.dto.TypeDto;
import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserRepository;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

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
    void shouldReturnEmptyList_whenNoTypesExist() {
        //given
        List<Type> typeList = new ArrayList<>();

        Mockito.when(typeRepositoryMock.findAll()).thenReturn(typeList);

        //when
        List<TypeDto> allTypes = typeService.findAllTypes();

        //then
        MatcherAssert.assertThat(allTypes, hasSize(0));
    }

    @Test
    void shouldReturnAllTypes_whenRepositoryContainsManyTypes() {
        //given
        List<Type> typeList = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            Type type = new Type();
            type.setId((long)i);
            type.setName("meal nr " + i);
            typeList.add(type);
        }

        Mockito.when(typeRepositoryMock.findAll()).thenReturn(typeList);

        //when
        List<TypeDto> allTypes = typeService.findAllTypes();

        //then
        MatcherAssert.assertThat(allTypes, hasSize(150));
        MatcherAssert.assertThat(allTypes.get(0).getName(), equalTo("meal nr 0"));
        MatcherAssert.assertThat(allTypes.get(119).getName(), equalTo("meal nr 119"));
    }

    @Test
    void findPaginatedTypesList() {
    }

    @Test
    void shouldReturnEmptyListAfterDeleteType() {
        //given
        Type type1 = new Type();
        type1.setId(1L);
        type1.setName("Zupa");

        Mockito.when(typeRepositoryMock.findById(type1.getId())).thenReturn(Optional.of(type1));

        //when
        typeService.deleteType(type1.getId());

        //then
        ArgumentCaptor<Type> typeArgumentCaptor = ArgumentCaptor.forClass(Type.class);
        Mockito.verify(typeRepositoryMock).delete(typeArgumentCaptor.capture());

        Type capturedType = typeArgumentCaptor.getValue();
        assertThat(capturedType.getId(), is(type1.getId()));
    }

    @Test
    void shouldDeleteTypeAndAssociatedRecipes() {
        //given
        Type type = new Type();
        type.setId(1L);
        type.setName("Zupa");

        Recipe recipe1 = new Recipe();
        recipe1.setId(1L);
        recipe1.setType(type);
        Set<Rating> ratings1 = new HashSet<>();
        ratings1.add(new Rating());
        recipe1.setRatings(ratings1);

        Set<Comment> comments1 = new HashSet<>();
        comments1.add(new Comment());
        recipe1.setComments(comments1);

        User user = new User();
        Set<User> favourites = new HashSet<>();
        favourites.add(user);
        recipe1.setFavourites(favourites);

        Recipe recipe2 = new Recipe();
        recipe2.setId(2L);
        recipe2.setType(type);
        Set<Rating> ratings2 = new HashSet<>();
        ratings2.add(new Rating());
        recipe2.setRatings(ratings2);

        Set<Comment> comments2 = new HashSet<>();
        comments2.add(new Comment());
        recipe2.setComments(comments2);

        List<Recipe> recipes = List.of(recipe1, recipe2);

        user.getFavoriteRecipes().add(recipe1);
        Mockito.when(userRepositoryMock.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.when(typeRepositoryMock.findById(type.getId())).thenReturn(Optional.of(type));
        Mockito.when(recipeRepositoryMock.findAllByType_Id(type.getId())).thenReturn(recipes);

        //when
        typeService.deleteType(type.getId());

        //then
        Mockito.verify(ratingRepositoryMock, Mockito.times(2)).deleteAll(Mockito.anySet());
        Mockito.verify(commentRepositoryMock, Mockito.times(2)).deleteAll(Mockito.anySet());

        ArgumentCaptor<Set> ratingsCaptor = ArgumentCaptor.forClass(Set.class);
        ArgumentCaptor<Set> commentsCaptor = ArgumentCaptor.forClass(Set.class);

        Mockito.verify(ratingRepositoryMock, Mockito.times(2)).deleteAll(ratingsCaptor.capture());
        Mockito.verify(commentRepositoryMock, Mockito.times(2)).deleteAll(commentsCaptor.capture());

        List<Set> capturedRatings = ratingsCaptor.getAllValues();
        List<Set> capturedComments = commentsCaptor.getAllValues();

        assertThat(capturedRatings.size(), is(2));
        assertThat(capturedComments.size(), is(2));

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepositoryMock, Mockito.times(1)).save(userArgumentCaptor.capture());
        List<User> allValues = userArgumentCaptor.getAllValues();
        assertThat(allValues.size(), is(1));

        Mockito.verify(typeRepositoryMock).delete(type);
    }


    @Test
    void updateType() {
    }

    @Test
    void findTypeById() {
    }
}