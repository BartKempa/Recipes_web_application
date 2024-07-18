package com.example.recipes.web;

import com.example.recipes.domain.comment.CommentService;
import com.example.recipes.domain.comment.dto.CommentDto;
import com.example.recipes.domain.user.UserService;
import com.example.recipes.domain.user.dto.UserRegistrationDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    private final static String USER_NOTIFICATION_ATTRIBUTE = "userNotification";
    private final static String COMMENT_SORT_FILED = "creationDate";
    private final UserService userService;
    private final CommentService commentService;

    public UserController(UserService userService, CommentService commentService) {
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping("/uzytkownik")
    public String getUserPanel(Model model, Authentication authentication){
        String currentUserEmail = authentication.getName();
        UserRegistrationDto user = userService.findUserByName(currentUserEmail).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("user", user);
        return "user-panel";
    }

    @GetMapping("/uzytkownik/aktualizacja/{userId}")
    public String getUpdateUserForm(@PathVariable(value = "userId") long userId,
                                    Model model){
        UserRegistrationDto user = userService.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("user", user);
        return "user-update-form";
    }

    @PostMapping("/uzytkownik/aktualizacja")
    public String updateDataUser(UserRegistrationDto user,
                                 RedirectAttributes redirectAttributes){
        userService.updateUserData(user);
        redirectAttributes.addFlashAttribute(
                USER_NOTIFICATION_ATTRIBUTE,
                "Dane użytkownika %s zostały zaktualizowane".formatted(user.getNickName())
        );
        return "redirect:/uzytkownik";
    }

    @GetMapping("/uzytkownik/aktualizacja-do-logowania/{userId}")
    public String getUpdateUserPasswordForm(@PathVariable(value = "userId") long userId,
                                            Model model){
        UserRegistrationDto user = userService.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("user", user);
        return "user-update-login-data-form";
    }

    @PostMapping("/uzytkownik/aktualizacja-logowanie")
    public String updateUserDataLogin(UserRegistrationDto user){
        userService.updateUserPassword(user);
        return "redirect:/logout";
    }

    @GetMapping("/uzytkownik/usuwanie-konta/{userId}")
    public String deleteUserForm(@PathVariable(value = "userId") long userId,
                                 Model model){
        UserRegistrationDto user = userService.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("user", user);
        return "user-delete-account";
    }

    @PostMapping("/uzytkownik/usuwanie-konta")
    public String deleteUser(Authentication authentication){
        String currentUserEmail = authentication.getName();
        userService.deleteUser(currentUserEmail);
        return "redirect:/logout";
    }

    @GetMapping("/uzytkownik/komentarze/{pageNo}")
    public String getUserComments(@PathVariable Optional<Integer> pageNo,
                                  Authentication authentication,
                                  Model model){
        String currentEmail = authentication.getName();
        int pageNumber = pageNo.orElse(1);
        Page<CommentDto> allUserCommentsPages = commentService.findAllUserComments(currentEmail, pageNumber, RecipeController.PAGE_SIZE, COMMENT_SORT_FILED);
        List<CommentDto> comments = allUserCommentsPages.getContent();
        int totalPages = allUserCommentsPages.getTotalPages();
        model.addAttribute("comments", comments);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("baseUrl", "/uzytkownik/komentarze");
        model.addAttribute("heading", "Twoje komentarze");
        return "user-comments";
    }

    @PostMapping("/uzytkownik/komentarze")
    public String deleteComment(@RequestParam(value = "id") Long id,
                                @RequestHeader String referer){
        commentService.deleteComment(id);
        return "redirect:" + referer;
    }

    @GetMapping("/uzytkownik/komentarze/edytuj/{id}")
    public String getEditCommentForm(@PathVariable(value = "id") Long id,
                                     Model model){
        CommentDto comment = userService.findUsersCommentById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("comment", comment);
        return "user-update-comment";
    }

    @PostMapping("/uzytkownik/komentarze/edytuj")
    public String editComment(CommentDto comment){
        userService.updateUsersComment(comment);
        return "redirect:/uzytkownik/komentarze/1";
    }
}
