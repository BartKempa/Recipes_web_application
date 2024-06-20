package com.example.recipes.web;

import com.example.recipes.domain.comment.CommentService;
import org.springframework.stereotype.Controller;

@Controller
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
}
