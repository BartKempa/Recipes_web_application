package com.example.recipes.web.admin;

import com.example.recipes.domain.difficultyLevel.DifficultyLevelService;
import com.example.recipes.domain.difficultyLevel.dto.DifficultyLevelDto;
import com.example.recipes.domain.recipe.dto.RecipeSaveDto;
import com.example.recipes.domain.type.TypeService;
import com.example.recipes.domain.type.dto.TypeDto;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RecipeManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TypeService typeService;

    @Autowired
    private DifficultyLevelService difficultyLevelService;

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetAddRecipeForm() throws Exception {
        mockMvc.perform(get("/admin/dodaj-przepis")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/recipe-form"))
                .andExpect(model().attributeExists("recipe"))
                .andExpect(model().attribute("recipe", Matchers.instanceOf(RecipeSaveDto.class)))
                .andExpect(model().attributeExists("types"))
                .andExpect(model().attributeExists("allDifficultyLevelDto"));
    }

    @Test
    void addRecipe() {
    }

    @Test
    void getRecipesList() {
    }

    @Test
    void updateRecipeForm() {
    }

    @Test
    void updateRecipe() {
    }

    @Test
    void deleteRecipe() {
    }
}