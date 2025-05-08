package com.example.recipes.web;

import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    void getRetrievePasswordForm() {
    }

    @Test
    void retrievePassword() {
    }
}