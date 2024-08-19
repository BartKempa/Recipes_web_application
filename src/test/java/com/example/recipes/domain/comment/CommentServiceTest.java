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

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void getActiveCommentsForRecipe() {
    }

    @Test
    void findAllUserComments() {
    }

    @Test
    void countUserComments() {
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