package com.example.recipes.web;

import com.example.recipes.domain.comment.dto.CommentDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CommentControllerTest {

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

    @Test
    void shouldRedirectToLoginWhenUserIsNotAuthenticated() throws Exception {
        //given
        long recipeId = 1L;

        //when
        mockMvc.perform(post("/dodaj-komentarz")
                        .param("text", "Testowy komentarz")
                        .param("recipeId", String.valueOf(recipeId))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldAllowAddingMultipleCommentsBySameUser() throws Exception {
        //given
        long recipeId = 1L;

        //when
        mockMvc.perform(post("/dodaj-komentarz")
                        .param("text", "Komentarz 1")
                        .param("recipeId", String.valueOf(recipeId))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/dodaj-komentarz")
                        .param("text", "Komentarz 2")
                        .param("recipeId", String.valueOf(recipeId))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        //then
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
        assertTrue(recipe.getComments().stream().anyMatch(c -> c.getText().equals("Komentarz 1")));
        assertTrue(recipe.getComments().stream().anyMatch(c -> c.getText().equals("Komentarz 2")));
    }
}

