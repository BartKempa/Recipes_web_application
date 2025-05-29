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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.example.recipes.web.RecipeController.PAGE_SIZE;
import static com.example.recipes.web.RecipeController.SORT_FIELD_MAP;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.notNullValue;
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
        mockMvc.perform(get("/uzytkownik/ulubione/strona/{pageNo}", pageNo)
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
                .andExpect(model().attribute("baseUrl", "/uzytkownik/ulubione/strona"));

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
        mockMvc.perform(get("/uzytkownik/ulubione/strona/{pageNo}", pageNo)
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
                .andExpect(model().attribute("baseUrl", "/uzytkownik/ulubione/strona"));

        //then
        assertTrue(recipes.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"creationDate", "name", "rating"})
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetFavouriteRecipesForUserWithDifferentSorting(String poleSortowania) throws Exception {
        //given
        int pageNo = 1;
        String sortField = RecipeController.SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
        Page<RecipeMainInfoDto> recipePage = recipeService.findFavouriteRecipesForUser("user@mail.com", pageNo, PAGE_SIZE, sortField);
        List<RecipeMainInfoDto> recipes = recipePage.getContent();

        //when
        mockMvc.perform(get("/uzytkownik/ulubione/strona/{pageNo}", pageNo)
                        .param("poleSortowania", poleSortowania)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe-listing"))
                .andExpect(model().attributeExists("recipes"))
                .andExpect(model().attribute("recipes", recipes))
                .andExpect(model().attribute("totalPages", recipePage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("heading", "Twoje ulubione przepisy"))
                .andExpect(model().attribute("baseUrl", "/uzytkownik/ulubione/strona"));

        //then
        assertFalse(recipes.isEmpty());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetRatedRecipesForUser() throws Exception {
        //given
        int pageNo = 1;
        String poleSortowania = "creationDate";
        String sortField = RecipeController.SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
        Page<RecipeMainInfoDto> recipePage = recipeService.findRatedRecipesByUser("user@mail.com", pageNo, PAGE_SIZE, sortField);
        List<RecipeMainInfoDto> recipes = recipePage.getContent();

        //when
        mockMvc.perform(get("/uzytkownik/ocenione/strona/{pageNo}", pageNo)
                        .param("poleSortowania", "creationDate")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe-listing"))
                .andExpect(model().attributeExists("recipes"))
                .andExpect(model().attribute("recipes", recipes))
                .andExpect(model().attribute("totalPages", recipePage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("heading", "Twoje oceninone przepisy"))
                .andExpect(model().attribute("baseUrl", "/uzytkownik/ocenione/strona"));

        //then
        assertFalse(recipes.isEmpty());
    }

    @Test
    @WithMockUser(username = "userWithoutRatings@mail.com", roles = "USER")
    void shouldGetEmptyListOfRatedRecipesForUser() throws Exception {
        //given
        int pageNo = 1;
        String poleSortowania = "creationDate";
        String sortField = RecipeController.SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
        Page<RecipeMainInfoDto> recipePage = recipeService.findRatedRecipesByUser("userWithoutRatings@mail.com", pageNo, PAGE_SIZE, sortField);
        List<RecipeMainInfoDto> recipes = recipePage.getContent();

        //when
        mockMvc.perform(get("/uzytkownik/ocenione/strona/{pageNo}", pageNo)
                        .param("poleSortowania", "creationDate")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe-listing"))
                .andExpect(model().attributeExists("recipes"))
                .andExpect(model().attribute("recipes", recipes))
                .andExpect(model().attribute("totalPages", recipePage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("heading", "Twoje oceninone przepisy"))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("baseUrl", "/uzytkownik/ocenione/strona"));

        //then
        assertTrue(recipes.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"creationDate", "name", "rating"})
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetRatedRecipesForUserWithDifferentSorting(String poleSortowania) throws Exception {
        //given
        int pageNo = 1;
        String sortField = RecipeController.SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
        Page<RecipeMainInfoDto> recipePage = recipeService.findRatedRecipesByUser("user@mail.com", pageNo, PAGE_SIZE, sortField);
        List<RecipeMainInfoDto> recipes = recipePage.getContent();

        //when
        mockMvc.perform(get("/uzytkownik/ocenione/strona/{pageNo}", pageNo)
                        .param("poleSortowania", poleSortowania)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe-listing"))
                .andExpect(model().attributeExists("recipes"))
                .andExpect(model().attribute("recipes", recipes))
                .andExpect(model().attribute("totalPages", recipePage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("heading", "Twoje oceninone przepisy"))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("baseUrl", "/uzytkownik/ocenione/strona"));

        //then
        assertFalse(recipes.isEmpty());
    }

    @Test
    void shouldReturnPdfWithCorrectHeadersAndBody() throws Exception {
        //given
        final long recipeId = 1L;

        //when
        //then
        mockMvc.perform(get("/przepis/{id}/pdf", recipeId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.containsString("attachment")))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF))
                .andExpect(result -> {
                    byte[] content = result.getResponse().getContentAsByteArray();
                    assertThat(content).isNotNull();
                    assertThat(content.length).isGreaterThan(100);
                });
    }

    @Test
    void shouldReturn404ForNonExistingRecipe() throws Exception {
        long nonExistingId = 99999L;
        mockMvc.perform(get("/przepis/{id}/pdf", nonExistingId))
                .andExpect(status().isNotFound());
    }
}