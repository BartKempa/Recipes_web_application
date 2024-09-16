package com.example.recipes.web;

import com.example.recipes.domain.rating.Rating;
import com.example.recipes.domain.rating.RatingService;
import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RatingService ratingService;


    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldAddRecipeRating() throws Exception {
        //given
        long recipeId = 4L;
        int rating = 3;
        String referer = "/some-page";
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
        int ratingSize = recipe.getRatings().size();

        //when
        mockMvc.perform(post("/ocen-przepis")
                .param("recipeId", String.valueOf(recipeId))
                .param("rating", String.valueOf(rating))
                .header("referer", referer)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(referer));

        //then
        recipe = recipeRepository.findById(recipeId).orElseThrow();
        assertEquals(ratingSize + 1, recipe.getRatings().size());
    }


}