package com.example.recipes.web.admin;

import com.example.recipes.domain.difficultyLevel.DifficultyLevelService;
import com.example.recipes.domain.difficultyLevel.dto.DifficultyLevelDto;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeSaveDto;
import com.example.recipes.domain.type.TypeService;
import com.example.recipes.domain.type.dto.TypeDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private RecipeService recipeService;

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
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAddRecipe() throws Exception {
        //given
        File file = new File("src/test/resources/static/img/meal.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "image",
                "meal.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                Files.readAllBytes(file.toPath())
        );

        RecipeSaveDto recipe = new RecipeSaveDto();
        recipe.setName("Zupa grzybowa");
        recipe.setDirectionsSteps("Zagotować wodę i dodać grzyby");
        recipe.setServing(5);
        recipe.setDescription("Bardzo dobra zupka");
        recipe.setCookingTime(10);
        recipe.setPreparationTime(10);
        recipe.setIngredients("Woda, grzyby, sól");
        recipe.setType("Zupy");
        recipe.setDifficultyLevel("Trudne");
        recipe.setImage(multipartFile);

        List<TypeDto> types = typeService.findAllTypes();
        List<DifficultyLevelDto> allDifficultyLevelDto = difficultyLevelService.findAllDifficultyLevelDto();

        //when
        //then
        mockMvc.perform(post("/admin/dodaj-przepis")
                .flashAttr("recipe", recipe)
                .flashAttr("types", types)
                .flashAttr("allDifficultyLevelDto", allDifficultyLevelDto)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }


    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetRecipesList() throws Exception {
        //given
        final Integer pageNo = 1;
        final String poleSortowania = "creationDate";
        final String sortDir = "asc";
        String sortField = RecipeManagementController.SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
        Page<RecipeMainInfoDto> recipePage = recipeService.findPaginatedRecipesList(pageNo, RecipeManagementController.PAGE_SIZE, sortField, sortDir);
        List<RecipeMainInfoDto> recipes = recipePage.getContent();
        //when
        //then
        mockMvc.perform(get("/admin/lista-przepisow/{pageNo}", pageNo)
                .param("poleSortowania", "creationDate")
                .param("sortDir", sortDir)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-recipe-list"))
                .andExpect(model().attributeExists("recipes"))
                .andExpect(model().attribute("recipes", recipes))
                .andExpect(model().attribute("totalPages", recipePage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("heading", "Lista przepisów"))
                .andExpect(model().attribute("sortField", sortField))
                .andExpect(model().attribute("baseUrl", "/admin/lista-przepisow"));
    }

    @Test
    void shouldGetRedirectToLoginPageWhenTryGetRecipesListAndUserIsNotAuthenticated() throws Exception {
        //given
        final Integer pageNo = 1;
        final String sortDir = "asc";

        //when
        //then
        mockMvc.perform(get("/admin/lista-przepisow/{pageNo}", pageNo)
                        .param("poleSortowania", "creationDate")
                        .param("sortDir", sortDir)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetUpdateRecipeForm() throws Exception {
        //given
        final long recipeIdToUpdate = 1L;
        assertTrue(recipeService.findRecipeById(recipeIdToUpdate).isPresent());

        //when
        //then
        mockMvc.perform(get("/admin/aktualizuj-przepis/{recipeId}", recipeIdToUpdate)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/recipe-update-form"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetStatusNotFoundWhenRecipeNotExistsAndTryGetUpdateRecipeForm() throws Exception {
        //given
        final long notExistsRecipeId = 111L;
        assertFalse(recipeService.findRecipeById(notExistsRecipeId).isPresent());

        //when
        //then
        mockMvc.perform(get("/admin/aktualizuj-przepis/{recipeId}", notExistsRecipeId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldUpdateRecipe() throws Exception {
        //given
        final long recipeIdToUpdate = 1L;
        File file = new File("src/test/resources/static/img/meal.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "image",
                "meal.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                Files.readAllBytes(file.toPath())
        );

        String name = "Zupa grzybowa";
        String directionsSteps = "Zagotować wodę i dodać grzyby";
        String description = "Bardzo dobra zupka";
        Integer serving = 5;
        Integer cookingTime = 10;
        Integer preparationTime = 10;
        String ingredients = "Woda, grzyby, sól";
        String type = "Zupy";
        String difficultyLevel = "Trudne";

        //when
        mockMvc.perform(multipart("/admin/aktualizuj-przepis")
                .file(multipartFile)
                .param("id", String.valueOf(recipeIdToUpdate))
                .param("name", name)
                .param("directionsSteps", directionsSteps)
                .param("serving", serving.toString())
                .param("description", description)
                .param("cookingTime", cookingTime.toString())
                .param("preparationTime", preparationTime.toString())
                .param("ingredients", ingredients)
                .param("type", type)
                .param("difficultyLevel", difficultyLevel)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }


    @Test
    void deleteRecipe() {

    }
}