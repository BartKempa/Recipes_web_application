package com.example.recipes.web;

import com.example.recipes.domain.comment.CommentService;
import com.example.recipes.domain.comment.dto.CommentDto;
import com.example.recipes.domain.rating.RatingService;
import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.user.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private RecipeController recipeController;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetRecipe() throws Exception {
        //given
        final long recipeId = 1L;
        RecipeFullInfoDto recipeFullInfoDto = recipeService.findRecipeById(recipeId).orElseThrow();
        Integer rating = ratingService.getRatingForRecipe("user@mail.com", recipeId).orElse(0);
        CommentDto comment = new CommentDto();
        int favorites = userService.favoritesCount(recipeId);
        List<CommentDto> comments = commentService.getActiveCommentsForRecipe(recipeId);

        //when
        //then
        mockMvc.perform(get("/przepis/{id}", recipeId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe"))
                .andExpect(model().attribute("recipe", recipeFullInfoDto))
                .andExpect(model().attribute("userRating", rating))
                .andExpect(model().attribute("comment", comment))
                .andExpect(model().attribute("comments", comments))
                .andExpect(model().attribute("favourites", favorites));
    }


    @Test
    void getAllRecipesPageable() {
    }

    @Test
    void getRecipesBySearchText() {
    }

    @Test
    void getFavouriteRecipesForUser() {
    }

    @Test
    void getRatedRecipesForUser() {
    }
}