package com.example.recipes.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ForgotPasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getLinkForm() throws Exception {
        mockMvc.perform(get("/odzyskiwanie-hasla")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("get-link-to-retrieve-password-form"));
    }

    @Test
    void getLinkToRetrievePassword() {
    }

    @Test
    void getRetrievePasswordForm() {
    }

    @Test
    void retrievePassword() {
    }
}