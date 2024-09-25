package com.example.recipes.web;

import com.example.recipes.domain.comment.CommentService;
import com.example.recipes.domain.comment.dto.CommentDto;
import com.example.recipes.domain.rating.RatingService;
import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.user.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.example.recipes.web.RecipeController.PAGE_SIZE;
import static com.example.recipes.web.RecipeController.SORT_FIELD_MAP;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
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
    void shouldGetAllRecipesPageable() throws Exception {
        //given
        int pageNo = 1;
        String poleSortowania = "creationDate";
        String sortField = RecipeController.SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
        Page<RecipeMainInfoDto> recipePage = recipeService.findPaginated(pageNo, PAGE_SIZE, sortField);
        List<RecipeMainInfoDto> recipes = recipePage.getContent();

        //when
        //then
        mockMvc.perform(get("/strona/{pageNo}", pageNo)
                        .param("poleSortowania", "creationDate")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe-listing"))
                .andExpect(model().attributeExists("recipes"))
                .andExpect(model().attribute("recipes", recipes))
                .andExpect(model().attribute("totalPages", recipePage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("heading", "Wszytskie przepisy"))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("baseUrl", "/strona"));
    }

        @Test
        void shouldGetRecipesBySearchText() throws Exception {
            //given
            int pageNo = 1;
            String searchText = "kurczak";
            String poleSortowania = "creationDate";
            String sortField = RecipeController.SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
            Page<RecipeMainInfoDto> recipePage = recipeService.findRecipesByText(searchText, pageNo, PAGE_SIZE, sortField);
            List<RecipeMainInfoDto> recipes = recipePage.getContent();

            //when
        mockMvc.perform(get("/szukaj/strona/{pageNo}", pageNo)
                        .param("poleSortowania", "creationDate")
                        .param("searchText", searchText)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe-listing"))
                .andExpect(model().attributeExists("recipes"))
                .andExpect(model().attribute("recipes", recipes))
                .andExpect(model().attribute("totalPages", recipePage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("heading", "Wyszukiwane przepisy"))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("baseUrl", "/szukaj/strona"));

            //then
            assertFalse(recipes.isEmpty());
    }
    @Test
    void shouldGetEmptyListOfRecipesBySearchText() throws Exception {
        //given
        int pageNo = 1;
        String searchText = "brakSlowaWBazie";
        String poleSortowania = "creationDate";
        String sortField = RecipeController.SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
        Page<RecipeMainInfoDto> recipePage = recipeService.findRecipesByText(searchText, pageNo, PAGE_SIZE, sortField);
        List<RecipeMainInfoDto> recipes = recipePage.getContent();

        //when
        mockMvc.perform(get("/szukaj/strona/{pageNo}", pageNo)
                        .param("poleSortowania", "creationDate")
                        .param("searchText", searchText)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe-listing"))
                .andExpect(model().attributeExists("recipes"))
                .andExpect(model().attribute("recipes", recipes))
                .andExpect(model().attribute("totalPages", recipePage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("heading", "Wyszukiwane przepisy"))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("baseUrl", "/szukaj/strona"));

        //then
        assertEquals(0, recipes.size());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetFavouriteRecipesForUser() throws Exception {
        //given
        int pageNo = 1;
        String poleSortowania = "creationDate";
        String sortField = RecipeController.SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
        Page<RecipeMainInfoDto> recipePage = recipeService.findFavouriteRecipesForUser("user@mail.com", pageNo, PAGE_SIZE, sortField);
        List<RecipeMainInfoDto> recipes = recipePage.getContent();

        //when
        mockMvc.perform(get("/ulubione/strona/{pageNo}", pageNo)
                        .param("poleSortowania", "creationDate")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe-listing"))
                .andExpect(model().attributeExists("recipes"))
                .andExpect(model().attribute("recipes", recipes))
                .andExpect(model().attribute("totalPages", recipePage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("heading", "Twoje ulubione przepisy"))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("baseUrl", "/ulubione/strona"));

        //then
        assertFalse(recipes.isEmpty());
    }

    @Test
    @WithMockUser(username = "userWithoutFavourites@mail.com", roles = "USER")
    void shouldGetEmptyListOfFavouriteRecipesForUser() throws Exception {
        //given
        int pageNo = 1;
        String poleSortowania = "creationDate";
        String sortField = RecipeController.SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
        Page<RecipeMainInfoDto> recipePage = recipeService.findFavouriteRecipesForUser("userWithoutFavourites@mail.com", pageNo, PAGE_SIZE, sortField);
        List<RecipeMainInfoDto> recipes = recipePage.getContent();

        //when
        mockMvc.perform(get("/ulubione/strona/{pageNo}", pageNo)
                        .param("poleSortowania", "creationDate")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe-listing"))
                .andExpect(model().attributeExists("recipes"))
                .andExpect(model().attribute("recipes", recipes))
                .andExpect(model().attribute("totalPages", recipePage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("heading", "Twoje ulubione przepisy"))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("baseUrl", "/ulubione/strona"));

        //then
        assertTrue(recipes.isEmpty());
    }

    @Test
    void getRatedRecipesForUser() {
    }
}