package com.example.recipes.domain.comment;

import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock private CommentRepository commentRepositoryMock;
    @Mock private UserRepository userRepositoryMock;
    @Mock private RecipeRepository recipeRepositoryMock;
    private CommentService commentService;

    @BeforeEach
    void init(){
        commentService = new CommentService(commentRepositoryMock, userRepositoryMock, recipeRepositoryMock);
    }


    @Test
    void getActiveCommentsForRecipe() {
    }

    @Test
    void addComment() {
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