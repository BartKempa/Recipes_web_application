package com.example.recipes.web.admin;

import com.example.recipes.domain.comment.CommentService;
import com.example.recipes.domain.comment.dto.CommentDto;
import com.example.recipes.domain.rating.RatingService;
import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.user.UserService;
import com.example.recipes.domain.user.dto.UserRegistrationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static com.example.recipes.web.admin.UserManagementController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RatingService ratingService;

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetUsersList() throws Exception {
        //given
        final Integer pageNo = 1;
        final String poleSortowania = "email";
        final String sortDir = "asc";
        String sortField = USER_SORT_FIELD_MAP.getOrDefault(poleSortowania, "email");
        Page<UserRegistrationDto> usersPage = userService.findAllUsers(pageNo, PAGE_SIZE, sortField, sortDir);
        List<UserRegistrationDto> users = usersPage.getContent();

        //when
        mockMvc.perform(get("/admin/list-uzytkownikow/{pageNo}", pageNo)
                .param("poleSortowania", poleSortowania)
                .param("sortDir", sortDir)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-user-list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", users))
                .andExpect(model().attribute("totalPages", usersPage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("heading", "Lista użytkoników"))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("sortDir", sortDir))
                .andExpect(model().attribute("baseUrl", "/admin/list-uzytkownikow"));

        //then
        assertFalse(users.isEmpty());
    }

    @Test
    void shouldRedirectToLoginPageWhenUserIsNotAuthenticatedAndTryGetUsersList() throws Exception {
        //given
        final Integer pageNo = 1;
        final String poleSortowania = "email";
        final String sortDir = "asc";

        //when
        //then
        mockMvc.perform(get("/admin/list-uzytkownikow/{pageNo}", pageNo)
                        .param("poleSortowania", poleSortowania)
                        .param("sortDir", sortDir)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "nickName"})
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetUsersListWithDifferentSortingArguments(String poleSortowania) throws Exception {
        //given
        final Integer pageNo = 1;
        final String sortDir = "asc";
        String sortField = USER_SORT_FIELD_MAP.getOrDefault(poleSortowania, "email");
        Page<UserRegistrationDto> usersPage = userService.findAllUsers(pageNo, PAGE_SIZE, sortField, sortDir);
        List<UserRegistrationDto> users = usersPage.getContent();

        //when
        mockMvc.perform(get("/admin/list-uzytkownikow/{pageNo}", pageNo)
                        .param("poleSortowania", poleSortowania)
                        .param("sortDir", sortDir)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-user-list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", users))
                .andExpect(model().attribute("totalPages", usersPage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("heading", "Lista użytkoników"))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("sortDir", sortDir))
                .andExpect(model().attribute("baseUrl", "/admin/list-uzytkownikow"));

        //then
        assertFalse(users.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"asc", "desc"})
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetUsersListWithDifferentSortingDirectionArguments(String sortDir) throws Exception {
        //given
        final Integer pageNo = 1;
        final String poleSortowania = "email";
        String sortField = USER_SORT_FIELD_MAP.getOrDefault(poleSortowania, "email");
        Page<UserRegistrationDto> usersPage = userService.findAllUsers(pageNo, PAGE_SIZE, sortField, sortDir);
        List<UserRegistrationDto> users = usersPage.getContent();

        //when
        mockMvc.perform(get("/admin/list-uzytkownikow/{pageNo}", pageNo)
                        .param("poleSortowania", poleSortowania)
                        .param("sortDir", sortDir)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-user-list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", users))
                .andExpect(model().attribute("totalPages", usersPage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("heading", "Lista użytkoników"))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("sortDir", sortDir))
                .andExpect(model().attribute("baseUrl", "/admin/list-uzytkownikow"));

        //then
        assertFalse(users.isEmpty());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetUserDetails() throws Exception {
        //given
        final long userId = 1L;
        UserRegistrationDto user = userService.findUserById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        final long commentCount = commentService.countUserComments(user.getEmail());
        final long favouriteRecipesCount = recipeService.countFavouriteUserRecipes(user.getEmail());
        final long ratedRecipesByUser = recipeService.countRatedRecipeByUser(user.getEmail());

        //when
        //then
        mockMvc.perform(get("/admin/uzytkownik/{userId}", userId)
                .with(csrf()))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("ratedRecipesByUser", ratedRecipesByUser))
                .andExpect(model().attribute("favouriteRecipesCount", favouriteRecipesCount))
                .andExpect(model().attribute("commentCount", commentCount))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-user-details"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetNotFoundStatusWhenUserNotExistsAndTryGetUserDetails() throws Exception {
        //given
        final long notExistsUserId = 111L;

        //when
        //then
        mockMvc.perform(get("/admin/uzytkownik/{userId}", notExistsUserId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetRedirectToLoginPageWhenUserNotAuthenticatedAndTryGetUserDetails() throws Exception {
        //given
        final long userId = 1L;

        //when
        //then
        mockMvc.perform(get("/admin/uzytkownik/{userId}", userId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetUserComments() throws Exception {
        //given
        final Integer pageNo = 1;
        final long userId = 1L;
        UserRegistrationDto user = userService.findUserById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<CommentDto> allUserCommentsPages = commentService.findAllUserComments(user.getEmail(), pageNo, PAGE_SIZE, SORT_FILED);
        List<CommentDto> comments = allUserCommentsPages.getContent();

        //when
        mockMvc.perform(get("/admin/uzytkownik/{userId}/komentarze/{pageNo}", pageNo, pageNo)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-user-comments"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attribute("comments", comments))
                .andExpect(model().attribute("totalPages", allUserCommentsPages.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("heading", "Komentarze użytkownika " + user.getNickName()))
                .andExpect(model().attribute("baseUrl", "/admin/uzytkownik/" + userId + "/komentarze"));

        //then
        assertFalse(comments.isEmpty());
    }

    @Test
    void shouldGetRedirectToLoginPageWhenUserNotAuthenticatedAndTryGetUserComments() throws Exception {
        //given
        final Integer pageNo = 1;
        final long userId = 1L;

        //when
        //then
        mockMvc.perform(get("/admin/uzytkownik/{userId}/komentarze/{pageNo}", userId, pageNo)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetNotFoundStatusWhenUserNotExistsAndTryGetUserComments() throws Exception {
        //given
        final Integer pageNo = 1;
        final long notExistsUserId = 111L;

        //when
        //then
        mockMvc.perform(get("/admin/uzytkownik/{userId}/komentarze/{pageNo}", notExistsUserId, pageNo)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

}