package com.example.recipes.web;

import com.example.recipes.domain.comment.Comment;
import com.example.recipes.domain.comment.CommentRepository;
import com.example.recipes.domain.comment.CommentService;
import com.example.recipes.domain.comment.dto.CommentDto;
import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserRepository;
import com.example.recipes.domain.user.dto.UserRegistrationDto;
import com.example.recipes.domain.user.dto.UserUpdateDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

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
        final long nonExistingUserId = 111L;

        //when
        //then
        mockMvc.perform(get("/uzytkownik/aktualizacja/{userId}", nonExistingUserId)
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
        final long nonExistingUserId = 222L;

        //when
        //then
        mockMvc.perform(get("/uzytkownik/aktualizacja-do-logowania/{userId}", nonExistingUserId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetDeleteUserForm() throws Exception {
        //given
        final long userId = 2L;

        //when
        //then
        mockMvc.perform(get("/uzytkownik/usuwanie-konta/{userId}", userId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", Matchers.instanceOf(UserRegistrationDto.class)))
                .andExpect(model().attribute("user", Matchers.hasProperty("id", Matchers.equalTo(userId))))
                .andExpect(view().name("user-delete-account"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        //given
        final long nonExistingUserId = 222L;

        //when
        //then
        mockMvc.perform(get("/uzytkownik/usuwanie-konta/{userId}", nonExistingUserId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldDeleteUser() throws Exception {
        //given
        final String email = "user@mail.com";
        User user = userRepository.findByEmail(email).orElseThrow();
        assertTrue(userRepository.existsById(user.getId()));

        //when
        mockMvc.perform(post("/uzytkownik/usuwanie-konta")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/logout"));

        //then
        assertFalse(userRepository.findByEmail(email).isPresent());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetUserComments() throws Exception {
        //given
        final int pageNo = 1;
        final int pageSize = 6;
        final String commentSortField = "creationDate";
        final String email = "user@mail.com";

        Page<CommentDto> allUserComments = commentService.findAllUserComments(email, pageNo, pageSize, commentSortField);
        List<CommentDto> comments = allUserComments.getContent();
        int totalPages = allUserComments.getTotalPages();

        //when
        //then
        mockMvc.perform(get("/uzytkownik/komentarze/{pageNo}", pageNo)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user-comments"))
                .andExpect(model().attribute("totalPages", totalPages))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("baseUrl", "/uzytkownik/komentarze"))
                .andExpect(model().attribute("heading", "Twoje komentarze"))
                .andExpect(model().attribute("comments", Matchers.hasSize(comments.size())))
                .andExpect(model().attribute("comments", comments));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldDeleteComment() throws Exception {
        //given
        final long commentId = 1L;
        final String referer = "/some-page";
        assertTrue(commentRepository.existsById(commentId));

        //when
        mockMvc.perform(post("/uzytkownik/komentarze")
                .param("commentId", String.valueOf(commentId))
                .header("referer", referer)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(referer));

        //then
        assertFalse(commentRepository.existsById(commentId));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetStatusNotFoundWhenCommentNotExists() throws Exception {
        //given
        final long notExistsCommentId = 111L;
        final String referer = "/some-page";
        assertFalse(commentRepository.findById(notExistsCommentId).isPresent());

        //when
        mockMvc.perform(post("/uzytkownik/komentarze")
                        .param("commentId", String.valueOf(notExistsCommentId))
                        .header("referer", referer)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetEditCommentForm() throws Exception {
        //given
        final long commentId = 1L;

        //when
        mockMvc.perform(get("/uzytkownik/komentarze/edytuj/{id}", commentId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user-update-comment"))
                .andExpect(model().attributeExists("comment"))
                .andExpect(model().attribute("comment", Matchers.instanceOf(CommentDto.class)));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetNotFoundStatusWhenCommentNotExistsAndTryGetEditCommentForm() throws Exception {
        //given
        final long notExistsCommentId = 111L;

        //when
        mockMvc.perform(get("/uzytkownik/komentarze/edytuj/{id}", notExistsCommentId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldEditComment() throws Exception {
        //given
        final long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setText("Komentarz po aktualizacji");

        //when
        mockMvc.perform(post("/uzytkownik/komentarze/edytuj")
                        .param("id", String.valueOf(commentDto.getId()))
                        .param("text", commentDto.getText())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uzytkownik/komentarze/1"));

        //then
        Comment updatedComment = commentRepository.findById(commentId).orElseThrow();
        assertEquals(commentDto.getText(), updatedComment.getText());
        assertEquals(commentDto.getId(), updatedComment.getId());
    }
}