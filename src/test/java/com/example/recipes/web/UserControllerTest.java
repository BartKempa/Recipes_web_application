package com.example.recipes.web;

import com.example.recipes.domain.user.dto.UserRegistrationDto;
import com.example.recipes.domain.user.dto.UserUpdateDto;
import org.hamcrest.Matchers;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetUserPanel() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/uzytkownik")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user-panel"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", Matchers.instanceOf(UserRegistrationDto.class)));
    }

    @Test
    void shouldRedirectToLoginPageWhenUserNotAuthenticated() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/uzytkownik")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetUpdateUserForm() throws Exception {
        //given
        final long userId = 1L;

        //when
        //then
        mockMvc.perform(get("/uzytkownik/aktualizacja/{userId}", userId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user-update-form"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", Matchers.instanceOf(UserUpdateDto.class)));
    }



    @Test
    void updateDataUser() {
    }

    @Test
    void getUpdateUserPasswordForm() {
    }

    @Test
    void updateUserDataLogin() {
    }

    @Test
    void deleteUserForm() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void getUserComments() {
    }

    @Test
    void deleteComment() {
    }

    @Test
    void getEditCommentForm() {
    }

    @Test
    void editComment() {
    }
}