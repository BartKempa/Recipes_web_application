package com.example.recipes.web;

import com.example.recipes.domain.comment.CommentService;
import com.example.recipes.domain.comment.dto.CommentDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/dodaj-komentarz")
    public String addRecipeComment(CommentDto comment,
                                   Authentication authentication,
                                   @RequestParam long recipeId,
                                   @RequestHeader String referer){
        String currentUserEmail = authentication.getName();
        commentService.addComment(comment, recipeId, currentUserEmail);
        return "redirect:" + referer;
    }

}
