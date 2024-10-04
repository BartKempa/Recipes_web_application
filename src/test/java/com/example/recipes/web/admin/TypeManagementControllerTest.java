package com.example.recipes.web.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TypeManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addTypeForm() {
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