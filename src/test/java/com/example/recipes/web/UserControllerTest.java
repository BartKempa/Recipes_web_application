package com.example.recipes.web;

import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserRepository;
import com.example.recipes.domain.user.dto.UserRegistrationDto;
import com.example.recipes.domain.user.dto.UserUpdateDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnNotFoundForNonExistingUserId() throws Exception {
        //given
        final long userId = 111L;

        //when
        //then
        mockMvc.perform(get("/uzytkownik/aktualizacja/{userId}", userId)
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldUpdateDataUser() throws Exception {
        //given
        final long userId = 2L;
        UserUpdateDto user = new UserUpdateDto();
        user.setId(userId);
        user.setFirstName("Janek");
        user.setLastName("Kowalski");
        user.setNickName("Janeczek");
        user.setAge(66);
        user.setPassword("Hardpass123!@#");
        assertTrue(userRepository.existsById(userId));

        //when
        mockMvc.perform(post("/uzytkownik/aktualizacja")
                        .param("id", String.valueOf(user.getId()))
                        .param("firstName", user.getFirstName())
                        .param("lastName", user.getLastName())
                        .param("nickName", user.getNickName())
                        .param("age", String.valueOf(user.getAge()))
                        .param("password", user.getPassword())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uzytkownik"));

        //then
        assertEquals("Janek", userRepository.findById(userId).orElseThrow().getFirstName());
        assertEquals("Kowalski", userRepository.findById(userId).orElseThrow().getLastName());
        assertEquals("Janeczek", userRepository.findById(userId).orElseThrow().getNickName());
        assertEquals(66, userRepository.findById(userId).orElseThrow().getAge());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldUpdateUserPassword() throws Exception {
        //given
        final long userId = 2L;
        UserUpdateDto user = new UserUpdateDto();
        user.setId(userId);
        user.setFirstName("Janek");
        user.setLastName("Janecki");
        user.setNickName("BigJohn");
        user.setAge(28);
        user.setPassword("Newpass123!@#");
        assertTrue(userRepository.existsById(userId));

        //when
        mockMvc.perform(post("/uzytkownik/aktualizacja-logowanie")
                        .param("id", String.valueOf(user.getId()))
                        .param("firstName", user.getFirstName())
                        .param("lastName", user.getLastName())
                        .param("nickName", user.getNickName())
                        .param("age", String.valueOf(user.getAge()))
                        .param("password", user.getPassword())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/logout"));

        //then
        User updatedUser = userRepository.findById(userId).orElseThrow();
        assertTrue(passwordEncoder.matches("Newpass123!@#", updatedUser.getPassword()));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnToUpdatePasswordFormWhenValidationFail() throws Exception {
        //given
        final long userId = 2L;
        User userBeforeUpdate = userRepository.findById(userId).orElseThrow();
        UserUpdateDto user = new UserUpdateDto();
        user.setId(userId);
        user.setPassword("wrongPass");
        assertTrue(userRepository.existsById(userId));

        //when
        mockMvc.perform(post("/uzytkownik/aktualizacja-logowanie")
                        .param("password", user.getPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("user", "password"))
                .andExpect(view().name("user-update-login-data-form"));

        //then
        User userAfterUpdate = userRepository.findById(userId).orElseThrow();
        assertEquals(userBeforeUpdate.getPassword(), userAfterUpdate.getPassword());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetUpdateUserPasswordForm() throws Exception {
        //given
        final long userId = 2L;

        //when
        //then
        mockMvc.perform(get("/uzytkownik/aktualizacja-do-logowania/{userId}", userId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user-update-login-data-form"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", Matchers.instanceOf(UserUpdateDto.class)))
                .andExpect(model().attribute("user", Matchers.hasProperty("id",Matchers.equalTo(userId))));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnNotFoundForNonExistingUserIdWhenTryUpdatePassword() throws Exception {
        //given
        final long userId = 222L;

        //when
        //then
        mockMvc.perform(get("/uzytkownik/aktualizacja-do-logowania/{userId}", userId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
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