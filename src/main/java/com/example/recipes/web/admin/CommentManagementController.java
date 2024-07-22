package com.example.recipes.web.admin;

import com.example.recipes.domain.comment.CommentService;
import org.springframework.stereotype.Controller;

@Controller
public class CommentManagementController {
    private final CommentService commentService;

    public CommentManagementController(CommentService commentService) {
        this.commentService = commentService;
    }
}
