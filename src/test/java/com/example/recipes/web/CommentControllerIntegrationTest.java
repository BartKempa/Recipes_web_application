package com.example.recipes.web;

import com.example.recipes.domain.comment.CommentService;
import com.example.recipes.domain.comment.dto.CommentDto;
import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserRepository;
import com.example.recipes.domain.user.UserService;
import com.example.recipes.domain.user.dto.UserRegistrationDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldAddRecipeComment() throws Exception {
        //given
        long recipeId = 1L;
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
        int commentsSize = recipe.getComments().size();
        CommentDto comment = new CommentDto();
        comment.setText("Nowy komentarz testowy");

        //when
        mockMvc.perform(post("/dodaj-komentarz")
                        .param("text", comment.getText())
                        .param("recipeId", String.valueOf(recipeId))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/przepis/" + recipeId));

        //then
        recipe = recipeRepository.findById(recipeId).orElseThrow();
        assertTrue(recipe.getComments().stream().anyMatch(c -> c.getText().equals(comment.getText())));
        assertEquals(commentsSize + 1, recipe.getComments().size());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldRejectCommentWithEmptyText() throws Exception {
        //given
        long recipeId = 1L;

        //when
        mockMvc.perform(post("/dodaj-komentarz")
                        .param("text", "")
                        .param("recipeId", String.valueOf(recipeId))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("comment", "text"))
                .andExpect(view().name("recipe"));
    }
}

