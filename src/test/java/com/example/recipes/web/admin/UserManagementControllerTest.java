package com.example.recipes.web.admin;

import com.example.recipes.domain.user.UserService;
import com.example.recipes.domain.user.dto.UserRegistrationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.recipes.web.admin.UserManagementController.PAGE_SIZE;
import static com.example.recipes.web.admin.UserManagementController.USER_SORT_FIELD_MAP;
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

    @Test
    void getUserDetails() {
    }

    @Test
    void getUserComments() {
    }

    @Test
    void getUserFavouriteRecipes() {
    }

    @Test
    void getUserRatedRecipes() {
    }
}