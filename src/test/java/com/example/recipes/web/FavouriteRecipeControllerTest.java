package com.example.recipes.web;

import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserRepository;
import com.example.recipes.domain.user.UserService;
import com.example.recipes.storage.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler;
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
@ActiveProfiles("test")
@Transactional
class FavouriteRecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserService userService;

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldAddToFavouriteRecipes() throws Exception {
        //given
        long recipeId = 1L;
        String referer = "/some-page";
        User user = userRepository.findByEmail("user@mail.com").orElseThrow();
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
        assertFalse(user.getFavoriteRecipes().contains(recipe));

        //when
        mockMvc.perform(post("/dodaj-do-ulubione")
                        .param("recipeId", String.valueOf(recipeId))
                        .header("referer", referer)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(referer));

        //then
        user = userRepository.findByEmail("user@mail.com").orElseThrow();
        assertTrue(user.getFavoriteRecipes().contains(recipe));
    }

    @Test
    void shouldRedirectToLoginPageWhenNotAuthenticated() throws Exception {
        //given
        long recipeId = 1L;
        String referer = "/some-page";

        //when
        mockMvc.perform(post("/dodaj-do-ulubione")
                .param("recipeId", String.valueOf(recipeId))
                .header("referer", referer)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}