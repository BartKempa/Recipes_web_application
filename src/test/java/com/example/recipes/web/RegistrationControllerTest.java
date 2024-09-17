package com.example.recipes.web;

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

import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        //then
        assertTrue(userRepository.findByEmail(userMail).isPresent());
    }
}