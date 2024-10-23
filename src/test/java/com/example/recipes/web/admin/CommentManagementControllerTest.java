package com.example.recipes.web.admin;

import com.example.recipes.domain.comment.CommentService;
import com.example.recipes.domain.comment.dto.CommentDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.example.recipes.web.RecipeController.PAGE_SIZE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CommentManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentService commentService;

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetCommentsList() throws Exception {
        //given
        int pagNo = 1;
        String poleSortowania = "approved";
        String sortDir = "asc";
        String sortFiled = CommentManagementController.COMMENT_SORT_FIELD_MAP.getOrDefault(poleSortowania, "approved");
        Page<CommentDto> commentsPage = commentService.findPaginatedCommentsList(pagNo, PAGE_SIZE, sortFiled, sortDir);
        List<CommentDto> comments = commentsPage.getContent();

        //when
        mockMvc.perform(get("/admin/lista-komentarzy/{pageNo}", pagNo)
                        .param("poleSortowania", poleSortowania)
                        .param("sortDir", sortDir)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-comment-list"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attribute("totalPages", commentsPage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pagNo))
                .andExpect(model().attribute("heading", "Lista komnetarzy"))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("sortDir", sortDir))
                .andExpect(model().attribute("baseUrl", "/admin/lista-komentarzy"));
        //then
        assertFalse(comments.isEmpty());
    }

    @Test
    void shouldNotGetCommentsListWhenUserIsNotAuthenticated() throws Exception {
        //given
        int pagNo = 1;
        String poleSortowania = "approved";
        String sortDir = "asc";

        //when
        //then
        mockMvc.perform(get("/admin/lista-komentarzy/{pageNo}", pagNo)
                        .param("poleSortowania", poleSortowania)
                        .param("sortDir", sortDir)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"creationDate", "approved"})
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetCommentsListWithDifferentSortingArgument(String poleSortowania) throws Exception {
        //given
        int pagNo = 1;
        String sortDir = "asc";
        String sortFiled = CommentManagementController.COMMENT_SORT_FIELD_MAP.getOrDefault(poleSortowania, "approved");
        Page<CommentDto> commentsPage = commentService.findPaginatedCommentsList(pagNo, PAGE_SIZE, sortFiled, sortDir);
        List<CommentDto> comments = commentsPage.getContent();

        //when
        mockMvc.perform(get("/admin/lista-komentarzy/{pageNo}", pagNo)
                        .param("poleSortowania", poleSortowania)
                        .param("sortDir", sortDir)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-comment-list"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attribute("totalPages", commentsPage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pagNo))
                .andExpect(model().attribute("heading", "Lista komnetarzy"))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("sortDir", sortDir))
                .andExpect(model().attribute("baseUrl", "/admin/lista-komentarzy"));

        //then
        assertFalse(comments.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"asc", "desc"})
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetCommentsListWithDifferentSortingDirection(String sortDir) throws Exception {
        //given
        int pagNo = 1;
        String poleSortowania = "approved";
        String sortFiled = CommentManagementController.COMMENT_SORT_FIELD_MAP.getOrDefault(poleSortowania, "approved");
        Page<CommentDto> commentsPage = commentService.findPaginatedCommentsList(pagNo, PAGE_SIZE, sortFiled, sortDir);
        List<CommentDto> comments = commentsPage.getContent();

        //when
        mockMvc.perform(get("/admin/lista-komentarzy/{pageNo}", pagNo)
                        .param("poleSortowania", poleSortowania)
                        .param("sortDir", sortDir)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-comment-list"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attribute("totalPages", commentsPage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pagNo))
                .andExpect(model().attribute("heading", "Lista komnetarzy"))
                .andExpect(model().attribute("sortField", poleSortowania))
                .andExpect(model().attribute("sortDir", sortDir))
                .andExpect(model().attribute("baseUrl", "/admin/lista-komentarzy"));
        //then
        assertFalse(comments.isEmpty());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldReturnEmptyCommentListWhenNoCommentsExist() throws Exception {
        //given
        int pagNo = 100;

        //when
        //then
        mockMvc.perform(get("/admin/lista-komentarzy/{pageNo}", pagNo)
                        .param("poleSortowania", "approved")
                        .param("sortDir", "asc")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-comment-list"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attribute("comments", Collections.emptyList()))
                .andExpect(model().attribute("currentPage", pagNo));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldApproveComment() throws Exception {
        //given
        final long commentNumber = 2L;
        final String referer = "/some-page";
        assertFalse(commentService.findCommentById(commentNumber).orElseThrow().isApproved());

        //when
        mockMvc.perform(post("/admin/lista-komentarzy/zatwierdz-komentarz")
                        .param("id", String.valueOf(commentNumber))
                        .header("referer", referer)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(referer));

        //then
        assertTrue(commentService.findCommentById(commentNumber).orElseThrow().isApproved());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetNotFoundStatusWhenCommentNotExists() throws Exception {
        //given
        final long notExistsCommentNumber = 111L;
        final String referer = "/some-page";
        assertFalse(commentService.findCommentById(notExistsCommentNumber).isPresent());

        //when
        //then
        mockMvc.perform(post("/admin/lista-komentarzy/zatwierdz-komentarz")
                        .param("id", String.valueOf(notExistsCommentNumber))
                        .header("referer", referer)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRedirectToLoginPageWhenUserIsNotAuthenticatedAndTryApproveComment() throws Exception {
        //given
        final long commentNumber = 2L;
        final String referer = "/some-page";
        assertFalse(commentService.findCommentById(commentNumber).orElseThrow().isApproved());

        //when
        //then
        mockMvc.perform(post("/admin/lista-komentarzy/zatwierdz-komentarz")
                        .param("id", String.valueOf(commentNumber))
                        .header("referer", referer)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldDeleteComment() throws Exception {
        //given
        final long commentNumber = 2L;
        final String referer = "/some-page";
        assertTrue(commentService.findCommentById(commentNumber).isPresent());

        //when
        mockMvc.perform(post("/admin/lista-komentarzy/usun")
                        .param("id", String.valueOf(commentNumber))
                        .header("referer", referer)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(referer));

        //then
        assertFalse(commentService.findCommentById(commentNumber).isPresent());
    }

    @Test
    void shouldRedirectToLoginPageWhenUserIsNotAuthenticatedAndTryDeleteComment() throws Exception {
        //given
        final long commentNumber = 2L;
        final String referer = "/some-page";
        assertTrue(commentService.findCommentById(commentNumber).isPresent());

        //when
        //then
        mockMvc.perform(post("/admin/lista-komentarzy/usun")
                        .param("id", String.valueOf(commentNumber))
                        .header("referer", referer)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetNotFoundStatusWhenDeleteNotExistsComment() throws Exception {
        //given
        final long notExistsCommentNumber = 111L;
        final String referer = "/some-page";
        assertFalse(commentService.findCommentById(notExistsCommentNumber).isPresent());

        //when
        //then
        mockMvc.perform(post("/admin/lista-komentarzy/usun")
                        .param("id", String.valueOf(notExistsCommentNumber))
                        .header("referer", referer)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}