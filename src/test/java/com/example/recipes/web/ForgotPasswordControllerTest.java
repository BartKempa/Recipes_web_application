package com.example.recipes.web;

import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserRepository;
import com.example.recipes.domain.user.dto.UserRetrievePasswordDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ForgotPasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    void getLinkForm() throws Exception {
        mockMvc.perform(get("/odzyskiwanie-hasla")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("get-link-to-retrieve-password-form"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetLinkToRetrievePassword() throws Exception {
        //given
        final String userMail = "user@mail.com";
        final String referer = "/some-page";
        assertTrue(userRepository.findByEmail(userMail).isPresent());

        //when
        mockMvc.perform(post("/odzyskiwanie-hasla")
                        .param("email", userMail)
                        .header("referer", referer)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(referer))
                .andExpect(flash().attribute("userNotification",
                "Link do zmiany hasła został wysłany na adres %s, link będzie ważny 5 minut".formatted(userMail)
                        ));

        //then
        User user = userRepository.findByEmail(userMail).orElseThrow();
        assertTrue(user.getPasswordResetTokenExpiry().isAfter(LocalDateTime.now()));
        assertFalse(user.getPasswordResetToken().isEmpty());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetNotificationWHenUserFillInWrongEmailToGetLinkToRetrievePassword() throws Exception {
        //given
        final String userMail = "wrongEmail@mail.com";
        final String referer = "/some-page";
        assertTrue(userRepository.findByEmail(userMail).isEmpty());

        //when
        mockMvc.perform(post("/odzyskiwanie-hasla")
                        .param("email", userMail)
                        .header("referer", referer)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(referer))
                .andExpect(flash().attribute("userNotification",
                        "Konto z podanym mailem nie zostało znalezione. Upewnij się, że podałeś prawidłowy adres email."
                ));
    }

    @Test
    @WithMockUser(username = "test@mail.com", roles = "USER")
    void shouldNotSendLinkToUnverifiedUser() throws Exception {
        // given
        final String userMail = "test@mail.com";
        final String referer = "/some-page";
        User user = userRepository.findByEmail(userMail).orElseThrow();
        assertFalse(user.isEmailVerified());

        // when
        mockMvc.perform(post("/odzyskiwanie-hasla")
                        .param("email", userMail)
                        .header("referer", referer)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(referer))
                .andExpect(flash().attribute("userNotification",
                        "Twoje konto nie zostało jeszcze aktywowane. Sprawdź skrzynkę e-mail i kliknij w link aktywacyjny."
                ));

        // then
        user = userRepository.findByEmail(userMail).orElseThrow();
        assertNull(user.getPasswordResetToken());
        assertNull(user.getPasswordResetTokenExpiry());
    }


    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailWhenTryGetRetrievePasswordFormWhenTokenNotExist() throws Exception {
        //given
        final String token = "not-exists-token";
        assertNull(userRepository.findByEmail("user@mail.com").orElseThrow().getPasswordResetToken());

        //when
        //then
        mockMvc.perform(get("/reset-hasla")
                .param("token", token)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/odzyskiwanie-hasla"))
                .andExpect(flash().attribute("userNotification",
                        "Token do zmiany hasła jest nieważny. Wyśli ponownie link do zmiany hasła."));
    }


    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailWhenTryGetRetrievePasswordFormWithExpiredToken() throws Exception {
        //given
        User user = userRepository.findByEmail("user@mail.com").orElseThrow();
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().minusHours(1));
        userRepository.save(user);

        //when
        //then
        mockMvc.perform(get("/reset-hasla")
                        .param("token", token)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/odzyskiwanie-hasla"))
                .andExpect(flash().attribute("userNotification",
                        "Token do zmiany hasła jest nieaktualny, upłynał termin jego ważności."));
    }


    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetRetrievePasswordFormWithValidToken() throws Exception {
        //given
        User user = userRepository.findByEmail("user@mail.com").orElseThrow();
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        //when
        //then
        mockMvc.perform(get("/reset-hasla")
                        .param("token", token)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("retrieve-password-form"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", Matchers.instanceOf(UserRetrievePasswordDto.class)));
    }


    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldRetrievePassword() throws Exception {
        //given
        User user = userRepository.findByEmail("user@mail.com").orElseThrow();
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String newPassword = "Password123#";
        String confirmPassword = "Password123#";
        UserRetrievePasswordDto userRetrievePasswordDto = new UserRetrievePasswordDto(user.getId(), newPassword, token);

        //when
        mockMvc.perform(post("/reset-hasla")
                .param("id", String.valueOf(userRetrievePasswordDto.getId()))
                .param("password", userRetrievePasswordDto.getPassword())
                .param("token", token)
                .param("confirmPassword", confirmPassword)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("userNotification",
                        "Hasło zostało pomyślnie zmienione, zaloguj się wykorzystując nowe hasło!"));

        //then
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertNull(updatedUser.getPasswordResetToken());
        assertNull(updatedUser.getPasswordResetTokenExpiry());
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
    }


    @Test
    void shouldFailWhenRetrievePasswordWithTwoDifferentPasswords() throws Exception {
        //given
        User user = userRepository.findByEmail("user@mail.com").orElseThrow();
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String newPassword = "Password123#";
        String confirmPassword = "WrongPassword123##";
        UserRetrievePasswordDto userRetrievePasswordDto = new UserRetrievePasswordDto(user.getId(), newPassword, token);

        //when
        mockMvc.perform(post("/reset-hasla")
                .param("id", String.valueOf(userRetrievePasswordDto.getId()))
                .param("password", userRetrievePasswordDto.getPassword())
                .param("token", token)
                .param("confirmPassword", confirmPassword)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reset-hasla?token=" + token))
                .andExpect(flash().attribute("userNotification",
                        "Hasła nie są jednakowe! Spróbuj ponownie."));
    }


    @Test
    void shouldReturnToRetrievePasswordFormWhenValidationFails() throws Exception {
        //given
        User user = userRepository.findByEmail("user@mail.com").orElseThrow();
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String newPassword = "pass";
        String confirmPassword = "pass";
        UserRetrievePasswordDto userRetrievePasswordDto = new UserRetrievePasswordDto(user.getId(), newPassword, token);

        //when
        mockMvc.perform(post("/reset-hasla")
                        .param("id", String.valueOf(userRetrievePasswordDto.getId()))
                        .param("password", userRetrievePasswordDto.getPassword())
                        .param("token", token)
                        .param("confirmPassword", confirmPassword)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("user", "password"))
                .andExpect(view().name("retrieve-password-form"));
    }


    @Test
    void shouldFailWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/reset-hasla")
                        .param("id", "1")
                        .param("password", "NewPass123!")
                        .param("token", "some-token")
                        .param("confirmPassword", "NewPass123!"))
                .andExpect(status().isForbidden());
    }
}