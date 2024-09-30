package com.example.recipes.web.admin;

import com.example.recipes.domain.comment.CommentService;
import com.example.recipes.domain.comment.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class CommentManagementController {
    private final static int PAGE_SIZE = 6;
    private final CommentService commentService;

    public CommentManagementController(CommentService commentService) {
        this.commentService = commentService;
    }

    protected final static Map<String, String> COMMENT_SORT_FIELD_MAP = new HashMap<>();

    static {
        COMMENT_SORT_FIELD_MAP.put("dataDodania", "creationDate");
        COMMENT_SORT_FIELD_MAP.put("zatwierdzenie", "approved");
    }

    @GetMapping("/admin/lista-komentarzy/{pageNo}")
    public String getCommentsList(@PathVariable Optional<Integer> pageNo,
                                  @RequestParam(value = "poleSortowania", required = false) String poleSortowania,
                                  @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                                  Model model) {
        int pageNumber = pageNo.orElse(1);
        String sortFiled = COMMENT_SORT_FIELD_MAP.getOrDefault(poleSortowania, "approved");
        Page<CommentDto> commentsPage = commentService.findPaginatedCommentsList(pageNumber, PAGE_SIZE, sortFiled, sortDir);
        List<CommentDto> comments = commentsPage.getContent();
        int totalPages = commentsPage.getTotalPages();
        model.addAttribute("comments", comments);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("heading", "Lista komnetarzy");
        model.addAttribute("sortField", poleSortowania);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("baseUrl", "/admin/lista-komentarzy");
        return "admin/admin-comment-list";
    }

    @PostMapping("/admin/lista-komentarzy/zatwierdz-komentarz")
    public String approveComment(@RequestParam(value = "id") Long id,
                                 @RequestHeader String referer) {
        commentService.approveComment(id);
        return "redirect:" + referer;
    }

    @PostMapping("/admin/lista-komentarzy/usun")
    public String deleteComment(@RequestParam long id,
                                @RequestHeader String referer) {
        commentService.deleteComment(id);
        return "redirect:" + referer;
    }

}
