package com.example.recipes.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Test
    void getUserPanel() {
    }

    @Test
    void getUpdateUserForm() {
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