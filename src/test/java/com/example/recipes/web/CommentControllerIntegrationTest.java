package com.example.recipes.web;

import com.example.recipes.domain.comment.CommentService;
import com.example.recipes.domain.recipe.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CommentControllerIntegrationTest {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;



}