package com.example.recipes.web;

import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserRepository;
import com.example.recipes.domain.user.dto.UserRegistrationDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRegisterForm() throws Exception {
        mockMvc.perform(get("/rejestracja")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-form"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", Matchers.instanceOf(UserRegistrationDto.class)));
    }

    @Test
    void shouldRegisterUserWithCorrectData() throws Exception {
        //given
        final String userMail = "testUser@mail.com";
        assertFalse(userRepository.findByEmail(userMail).isPresent());

        //when
        mockMvc.perform(post("/rejestracja")
                        .param("email", userMail)
                        .param("password", "hardPass123!@#")
                        .param("firstName", "Bart")
                        .param("lastName", "Bartkowski")
                        .param("nickName", "Barti")
                        .param("age", String.valueOf(40))
                        .param("emailVerificationToken", "123456789")
                        .param("emailVerificationTokenExpiry", String.valueOf(LocalDateTime.now().plusMinutes(10)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        //then
        assertTrue(userRepository.findByEmail(userMail).isPresent());
        assertFalse(userRepository.findByEmail(userMail).get().isEmailVerified());
    }

    @Test
    void shouldReturnToRegistrationFormWhenValidationFails() throws Exception {
        mockMvc.perform(post("/rejestracja")
                        .param("email", "")
                        .param("password", "pass")
                        .param("firstName", "a")
                        .param("lastName", " ")
                        .param("nickName", "Barti")
                        .param("age", String.valueOf(10))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("user", "email", "password", "firstName", "lastName", "age"))
                .andExpect(view().name("registration-form"));
    }

    @Test
    void shouldReturnToRegistrationFormWhenEmailAlreadyExists() throws Exception {
        //given
        final String existingEmail = "user@mail.com";
        assertTrue(userRepository.findByEmail(existingEmail).isPresent());

        //when
        //then
        mockMvc.perform(post("/rejestracja")
                        .param("email", existingEmail)
                        .param("password", "hardPass123!@#")
                        .param("firstName", "Bart")
                        .param("lastName", "Bartkowski")
                        .param("nickName", "Barti")
                        .param("age", String.valueOf(40))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("user", "email"))
                .andExpect(view().name("registration-form"));
    }

    @Test
    void shouldRegistrationFailWithoutCsrf() throws Exception {
        //given
        final String userMail = "testUser@mail.com";

        //when
        //then
        mockMvc.perform(post("/rejestracja")
                        .param("email", userMail)
                        .param("password", "hardPass123!@#")
                        .param("firstName", "Bart")
                        .param("lastName", "Bartkowski")
                        .param("nickName", "Barti")
                        .param("age", String.valueOf(40)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailWhenActivateAccountWithNotExistsToken() throws Exception {
        //given
        final String userToken = "not-exists-token";

        //when
        //then
        mockMvc.perform(get("/aktywacja-konta")
                .param("token", userToken)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("userNotification",
                        "Token do aktywacji konta jest nieważny."))
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void shouldFailWhenActivateAccountWithExpiredToken() throws Exception {
        //given
        final String userToken = "28be22c6-c750-4e13-987a-f31963ae9f07";

        //when
        //then
        mockMvc.perform(get("/aktywacja-konta")
                        .param("token", userToken)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("userNotification",
                        "Token do aktywacji konta jest nieaktualny, upłynał termin jego ważności."))
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void shouldActivateAccountWhenTokenIsValid() throws Exception {
        // given
        String token = UUID.randomUUID().toString();
        User user = new User();
        user.setEmail("validTokenUser@mail.com");
        user.setPassword("{noop}Pass123");
        user.setEmailVerified(false);
        user.setEmailverificationtoken(token);
        user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // when
        mockMvc.perform(get("/aktywacja-konta")
                        .param("token", token)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("userNotification",
                        "Konto zostało pomyślnie aktywowane, możesz się zalogować!"));

        // then
        User activatedUser = userRepository.findByEmail("validTokenUser@mail.com").orElseThrow();
        assertTrue(activatedUser.isEmailVerified());
        assertNull(activatedUser.getEmailVerificationToken());
        assertNull(activatedUser.getEmailVerificationTokenExpiry());
    }

    @Test
    @Transactional
    void shouldActivateNewAccountWithToken() throws Exception {
        // given
        final String userMail = "testUser@mail.com";

        mockMvc.perform(post("/rejestracja")
                        .param("email", userMail)
                        .param("password", "hardPass123!@#")
                        .param("firstName", "Bart")
                        .param("lastName", "Bartkowski")
                        .param("nickName", "Barti")
                        .param("age", "40")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        Optional<User> optionalUser = userRepository.findByEmail(userMail);
        assertTrue(optionalUser.isPresent());

        User user = optionalUser.get();
        String token = UUID.randomUUID().toString();
        user.setEmailverificationtoken(token);
        user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(1));
        user.setEmailVerified(false);
        userRepository.save(user);

        // when
        mockMvc.perform(get("/aktywacja-konta")
                        .param("token", token)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // then
        User updatedUser = userRepository.findByEmail(userMail).get();
        assertTrue(updatedUser.isEmailVerified());
        assertNull(updatedUser.getEmailVerificationToken());
        assertNull(updatedUser.getEmailVerificationTokenExpiry());
    }
}