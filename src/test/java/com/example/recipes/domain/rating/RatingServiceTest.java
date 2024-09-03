package com.example.recipes.domain.rating;

import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {
    @Mock private RatingRepository ratingRepositoryMock;
    @Mock private RecipeRepository recipeRepositoryMock;
    @Mock private UserRepository userRepositoryMock;
    private RatingService ratingService;

    @BeforeEach
    void init(){
        ratingService = new RatingService(ratingRepositoryMock, recipeRepositoryMock, userRepositoryMock);
    }

    @Test
    void shouldAddRating() {
        //given
        User user = new User();
        user.setId(10L);
        user.setEmail("user@example.com");

        Recipe recipe1 = new Recipe();
        recipe1.setId(1L);

        Rating rating = new Rating(user, recipe1, 5);

        Mockito.when(ratingRepositoryMock.findByUser_EmailAndRecipe_Id("user@example.com", 1L)).thenReturn(Optional.of(rating));
        Mockito.when(userRepositoryMock.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        Mockito.when(recipeRepositoryMock.findById(1L)).thenReturn(Optional.of(recipe1));

        //when
        ratingService.addOrUpdateRating("user@example.com", 1L, 5);

        ArgumentCaptor<Rating> ratingCaptor = ArgumentCaptor.forClass(Rating.class);
        Mockito.verify(ratingRepositoryMock).save(ratingCaptor.capture());

        //then
        Rating captorValue = ratingCaptor.getValue();
        assertEquals(captorValue.getRating(), 5);
    }

    @Test
    void shouldAddNewRatingWhenNoExistingRating() {
        //given
        User user = new User();
        user.setId(10L);
        user.setEmail("user@example.com");

        Recipe recipe1 = new Recipe();
        recipe1.setId(1L);

        Mockito.when(ratingRepositoryMock.findByUser_EmailAndRecipe_Id("user@example.com", 1L)).thenReturn(Optional.empty());
        Mockito.when(userRepositoryMock.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        Mockito.when(recipeRepositoryMock.findById(1L)).thenReturn(Optional.of(recipe1));

        //when
        ratingService.addOrUpdateRating("user@example.com", 1L, 4);

        ArgumentCaptor<Rating> ratingCaptor = ArgumentCaptor.forClass(Rating.class);
        Mockito.verify(ratingRepositoryMock).save(ratingCaptor.capture());

        //then
        Rating captorValue = ratingCaptor.getValue();
        assertEquals(captorValue.getRating(), 4);
        assertEquals(captorValue.getUser(), user);
        assertEquals(captorValue.getRecipe(), recipe1);
    }

    @Test
    void shouldUpdateExistingRating() {
        //given
        User user = new User();
        user.setId(10L);
        user.setEmail("user@example.com");

        Recipe recipe1 = new Recipe();
        recipe1.setId(1L);

        Rating rating = new Rating(user, recipe1, 3);

        Mockito.when(ratingRepositoryMock.findByUser_EmailAndRecipe_Id("user@example.com", 1L)).thenReturn(Optional.of(rating));
        Mockito.when(userRepositoryMock.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        Mockito.when(recipeRepositoryMock.findById(1L)).thenReturn(Optional.of(recipe1));

        //when
        ratingService.addOrUpdateRating("user@example.com", 1L, 5);

        ArgumentCaptor<Rating> ratingCaptor = ArgumentCaptor.forClass(Rating.class);
        Mockito.verify(ratingRepositoryMock).save(ratingCaptor.capture());

        //then
        Rating captorValue = ratingCaptor.getValue();
        assertEquals(captorValue.getRating(), 5);
        assertEquals(captorValue.getUser(), user);
        assertEquals(captorValue.getRecipe(), recipe1);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        //given
        Mockito.when(userRepositoryMock.findByEmail("user@example.com")).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(NoSuchElementException.class, () -> ratingService.addOrUpdateRating("user@example.com", 1L, 5));
    }

    @Test
    void shouldThrowExceptionWhenRecipeNotFound() {
        //given
        User user = new User();
        user.setId(10L);
        user.setEmail("user@example.com");

        Mockito.when(userRepositoryMock.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        Mockito.when(recipeRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(NoSuchElementException.class, () -> ratingService.addOrUpdateRating("user@example.com", 1L, 5));
    }

    @Test
    void shouldGetUserRatingForRecipe() {
        //given
        User user = new User();
        user.setId(10L);
        user.setEmail("user@example.com");

        Recipe recipe1 = new Recipe();
        recipe1.setId(1L);

        Rating rating1 = new Rating(user, recipe1, 3);

        Mockito.when(ratingRepositoryMock.findByUser_EmailAndRecipe_Id("user@example.com", 1L)).thenReturn(Optional.of(rating1));

        //when
        Integer rating = ratingService.getRatingForRecipe("user@example.com", 1L).orElseThrow();

        //then
        assertThat(rating, is(3));
    }

    @Test
    void shouldReturnEmptyOptionalWhenRatingNotFound() {
        //given
        Mockito.when(ratingRepositoryMock.findByUser_EmailAndRecipe_Id("user@example.com", 1L))
                .thenReturn(Optional.empty());

        //when
        Optional<Integer> rating = ratingService.getRatingForRecipe("user@example.com", 1L);

        //then
        assertTrue(rating.isEmpty());
    }

    @Test
    void shouldGetListOfUsersRating() {
        //given
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user@example.com");

        Recipe recipe1 = new Recipe();
        recipe1.setId(101L);

        Recipe recipe2 = new Recipe();
        recipe2.setId(102L);

        Rating rating1 = new Rating(user1, recipe1, 5);
        Rating rating2 = new Rating(user1, recipe2, 3);

        List<Rating> ratings = Arrays.asList(rating1, rating2);

        Mockito.when(ratingRepositoryMock.findAllByUser_Email("user@example.com")).thenReturn(ratings);

        //when
        Map<Long, Integer> userRatings = ratingService.getUserRatings("user@example.com");

        //then
        assertThat(userRatings.size(), is(2));
        assertThat(userRatings.get(recipe1.getId()), is(5));
        assertThat(userRatings.get(recipe2.getId()), is(3));
    }
}