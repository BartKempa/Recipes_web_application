package com.example.recipes.domain.rating;

import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {
    private RatingService ratingService;
    @Mock private RatingRepository ratingRepositoryMock;
    @Mock private RecipeRepository recipeRepositoryMock;
    @Mock private UserRepository userRepositoryMock;

    @BeforeEach
    void init(){
        ratingService = new RatingService(ratingRepositoryMock, recipeRepositoryMock, userRepositoryMock);
    }

    @Test
    void addOrUpdateRating() {



    }

    @Test
    void getRatingForRecipe() {
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