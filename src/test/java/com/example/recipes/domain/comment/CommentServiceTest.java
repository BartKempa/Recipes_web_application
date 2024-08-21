package com.example.recipes.domain.comment;

import com.example.recipes.domain.comment.dto.CommentDto;
import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock private CommentRepository commentRepositoryMock;
    @Mock private UserRepository userRepositoryMock;
    @Mock private RecipeRepository recipeRepositoryMock;
    @Mock private CommentDateTimeProvider dateTimeProvider;
    private CommentService commentService;

    @BeforeEach
    void init(){
        commentService = new CommentService(commentRepositoryMock, userRepositoryMock, recipeRepositoryMock, dateTimeProvider);
    }

    @Test
    void shouldAddNewComment() {

        //given
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        User user = new User();
        user.setEmail("user@example.com");

        LocalDateTime now = LocalDateTime.now();

        CommentDto commentDto = new CommentDto();
        commentDto.setId(10L);
        commentDto.setCreationDate(now);
        commentDto.setApproved(false);
        commentDto.setText("example comment");
        commentDto.setUserEmail("user@example.com");

        Mockito.when(recipeRepositoryMock.findById(1L)).thenReturn(Optional.of(recipe));
        Mockito.when(userRepositoryMock.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        Mockito.when(dateTimeProvider.getCurrentTime()).thenReturn(now);

        //when
        commentService.addComment(commentDto, recipe.getId(), user.getEmail());

        //then
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        Mockito.verify(commentRepositoryMock).save(commentCaptor.capture());

        Comment captorValue = commentCaptor.getValue();
        assertThat(captorValue.getText(), is(commentDto.getText()));
        assertThat(captorValue.getCreationDate(), is(now));
        assertThat(captorValue.getRecipe().getId(), is(1L));

    }

    @Test
    void shouldThrowExceptionWhenUserNotExists() {

        //given
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        CommentDto commentDto = new CommentDto();

        Mockito.when(recipeRepositoryMock.findById(1L)).thenReturn(Optional.of(recipe));

        //when
        //then
        assertThrows(NoSuchElementException.class, () -> commentService.addComment(commentDto, recipe.getId(), "user@example.com"));
    }

    @Test
    void shouldThrowExceptionWhenRecipeNotExists() {

        //given
        User user = new User();
        user.setEmail("user@example.com");

        CommentDto commentDto = new CommentDto();

        Mockito.when(recipeRepositoryMock.findById(2L)).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(NoSuchElementException.class, () -> commentService.addComment(commentDto, 2L, "user@example.com"));

    }

    @Test
    void shouldSetApprovedToFalse() {

        //given
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        User user = new User();
        user.setEmail("user@example.com");

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");

        LocalDateTime now = LocalDateTime.now();

        Mockito.when(recipeRepositoryMock.findById(1L)).thenReturn(Optional.of(recipe));
        Mockito.when(userRepositoryMock.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        Mockito.when(dateTimeProvider.getCurrentTime()).thenReturn(now);

        //when
        commentService.addComment(commentDto, recipe.getId(), user.getEmail());

        //then
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        Mockito.verify(commentRepositoryMock).save(commentCaptor.capture());

        Comment savedComment = commentCaptor.getValue();
        assertFalse(savedComment.isApproved());

    }

    @Test
    void shouldGetOneActiveCommentsForRecipe() {

        //given
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        User user = new User();
        user.setEmail("user@example.com");

        LocalDateTime now = LocalDateTime.now();

        Comment comment1 = new Comment();
        comment1.setId(10L);
        comment1.setCreationDate(now);
        comment1.setApproved(true);
        comment1.setText("example comment");
        comment1.setRecipe(recipe);
        comment1.setUser(user);

        Comment comment2 = new Comment();
        comment2.setId(11L);
        comment2.setCreationDate(now);
        comment2.setApproved(false);
        comment2.setText("second example comment");
        comment2.setRecipe(recipe);
        comment2.setUser(user);

        List<Comment> comments = List.of(comment1, comment2);

        Mockito.when(commentRepositoryMock.findAllByRecipeId(recipe.getId())).thenReturn(comments);

        //when
        List<CommentDto> activeCommentsForRecipe = commentService.getActiveCommentsForRecipe(1L);

        //then
        assertThat(activeCommentsForRecipe.size(), is(1));

    }

    @Test
    void shouldGetEmptyListOfActiveCommentsForRecipe() {

        //given
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        User user = new User();
        user.setEmail("user@example.com");

        LocalDateTime now = LocalDateTime.now();

        Comment comment1 = new Comment();
        comment1.setId(10L);
        comment1.setCreationDate(now);
        comment1.setApproved(false);
        comment1.setText("example comment");
        comment1.setRecipe(recipe);
        comment1.setUser(user);

        Comment comment2 = new Comment();
        comment2.setId(11L);
        comment2.setCreationDate(now);
        comment2.setApproved(false);
        comment2.setText("second example comment");
        comment2.setRecipe(recipe);
        comment2.setUser(user);

        List<Comment> comments = List.of(comment1, comment2);

        Mockito.when(commentRepositoryMock.findAllByRecipeId(recipe.getId())).thenReturn(comments);

        //when
        List<CommentDto> activeCommentsForRecipe = commentService.getActiveCommentsForRecipe(1L);

        //then
        assertThat(activeCommentsForRecipe.size(), is(0));
        assertTrue(activeCommentsForRecipe.isEmpty());

    }

    @Test
    void shouldMapCommentsToDtoCorrectly() {

        //given
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        User user = new User();
        user.setEmail("user@example.com");

        LocalDateTime now = LocalDateTime.now();

        Comment comment = new Comment();
        comment.setId(10L);
        comment.setCreationDate(now);
        comment.setApproved(true);
        comment.setText("example comment");
        comment.setRecipe(recipe);
        comment.setUser(user);

        Mockito.when(commentRepositoryMock.findAllByRecipeId(recipe.getId())).thenReturn(List.of(comment));

        //when
        List<CommentDto> activeCommentsForRecipe = commentService.getActiveCommentsForRecipe(1L);

        //then
        assertThat(activeCommentsForRecipe.size(), is(1));
        CommentDto dto = activeCommentsForRecipe.get(0);
        assertThat(dto.getText(), is("example comment"));
        assertThat(dto.isApproved(), is(true));
        assertThat(dto.getUserEmail(), is("user@example.com"));

    }

    @Test
    void shouldThrowExceptionWhenRepositoryFails() {

        //given
        long recipeId = 1L;
        Mockito.when(commentRepositoryMock.findAllByRecipeId(recipeId)).thenThrow(new RuntimeException("Database error"));

        //when/then
        assertThrows(RuntimeException.class, () -> commentService.getActiveCommentsForRecipe(recipeId));

    }

    @Test
    void shouldFindAllUserComments() {

        //given
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        User user = new User();
        user.setEmail("user@example.com");

        LocalDateTime now = LocalDateTime.now();

        Comment comment1 = new Comment();
        comment1.setId(10L);
        comment1.setCreationDate(now);
        comment1.setApproved(false);
        comment1.setText("example comment");
        comment1.setRecipe(recipe);
        comment1.setUser(user);

        Comment comment2 = new Comment();
        comment2.setId(11L);
        comment2.setCreationDate(now);
        comment2.setApproved(false);
        comment2.setText("second example comment");
        comment2.setRecipe(recipe);
        comment2.setUser(user);

        List<Comment> commentList = Arrays.asList(comment1, comment2);
        Page<Comment> commentPage = new PageImpl<>(commentList);


        Mockito.when(commentRepositoryMock.findAllUserComments(Mockito.eq("user@example.com"), Mockito.any(Pageable.class))).thenReturn(commentPage);

        int pageNumber = 1;
        int pageSize = 3;
        String sortField = "text";

        //when
        Page<CommentDto> allUserComments = commentService.findAllUserComments("user@example.com", pageNumber, pageSize, sortField);

        //then
        assertThat(allUserComments.getContent().get(0).getText(), is("example comment"));
        assertThat(allUserComments.getContent().get(0).getUserEmail(), is("user@example.com"));

        assertEquals(2, allUserComments.getTotalElements());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(commentRepositoryMock).findAllUserComments(Mockito.eq("user@example.com"), pageableCaptor.capture());

        Pageable captorValue = pageableCaptor.getValue();
        assertEquals(pageNumber -1, captorValue.getPageNumber());
        assertEquals(pageSize, captorValue.getPageSize());
        assertEquals(Sort.by(sortField).descending(), captorValue.getSort());

    }

    @Test
    void shouldGetTwoUserComments() {

        //given
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        User user = new User();
        user.setEmail("user@example.com");

        LocalDateTime now = LocalDateTime.now();

        Comment comment1 = new Comment();
        comment1.setId(10L);
        comment1.setCreationDate(now);
        comment1.setApproved(false);
        comment1.setText("example comment");
        comment1.setRecipe(recipe);
        comment1.setUser(user);

        Comment comment2 = new Comment();
        comment2.setId(11L);
        comment2.setCreationDate(now);
        comment2.setApproved(false);
        comment2.setText("second example comment");
        comment2.setRecipe(recipe);
        comment2.setUser(user);

        List<Comment> commentList = Arrays.asList(comment1, comment2);

        Mockito.when(commentRepositoryMock.findAllUserComments("user@example.com")).thenReturn(commentList);

        //when
        long countUserComments = commentService.countUserComments("user@example.com");

        //then
        assertEquals(countUserComments, commentList.size());
        Mockito.verify(commentRepositoryMock).findAllUserComments("user@example.com");

    }

    @Test
    void shouldGetEmptyListOfUserComments() {

        //given
        Mockito.when(commentRepositoryMock.findAllUserComments("otherUser@example.com")).thenReturn(Collections.emptyList());

        //when
        long countUserComments = commentService.countUserComments("otherUser@example.com");

        //then
        assertEquals(0, countUserComments);
        Mockito.verify(commentRepositoryMock).findAllUserComments("otherUser@example.com");

    }

    @Test
    void deleteComment() {
    }

    @Test
    void findPaginatedCommentsList() {
    }

    @Test
    void approveComment() {
    }
}