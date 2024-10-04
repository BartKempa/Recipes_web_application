package com.example.recipes.web.admin;

import com.example.recipes.domain.type.TypeService;
import com.example.recipes.domain.type.dto.TypeDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TypeManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TypeService typeService;

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetAddTypeForm() throws Exception {
        mockMvc.perform(get("/admin/dodaj-typ")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/type-form"))
                .andExpect(model().attributeExists("type"))
                .andExpect(model().attribute("type", Matchers.instanceOf(TypeDto.class)));
    }

    @Test
    void shouldRedirectToLoginPageWhenUserNotAuthorizedAndTryGetAddTypeForm() throws Exception {
        mockMvc.perform(get("/admin/dodaj-typ")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void addType() {
    }

    @Test
    void getTypesList() {
    }

    @Test
    void deleteType() {
    }

    @Test
    void updateTypeForm() {
    }

    @Test
    void updateType() {
    }
}