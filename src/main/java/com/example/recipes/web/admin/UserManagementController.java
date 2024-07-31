package com.example.recipes.web.admin;

import com.example.recipes.domain.comment.CommentService;
import com.example.recipes.domain.comment.dto.CommentDto;
import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.user.UserService;
import com.example.recipes.domain.user.dto.UserRegistrationDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class UserManagementController {
    private final static String COMMENT_SORT_FILED = "creationDate";
    private final static int PAGE_SIZE = 6;
    private final UserService userService;
    private final CommentService commentService;
    private final RecipeService recipeService;

    public UserManagementController(UserService userService, CommentService commentService, RecipeService recipeService) {
        this.userService = userService;
        this.commentService = commentService;
        this.recipeService = recipeService;
    }

    private final static Map<String, String> USER_SORT_FIELD_MAP = new HashMap<>();
    static {
        USER_SORT_FIELD_MAP.put("nazwaUzytkownika", "nickName");
        USER_SORT_FIELD_MAP.put("adresEmail", "email");
    }

    @GetMapping("/admin/list-uzytkownikow/{pageNo}")
    public String getUsersList(@PathVariable Optional<Integer> pageNo,
                               @RequestParam(value = "poleSortowania", required = false) String poleSortowania,
                               @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                               Model model){
        int pageNumber = pageNo.orElse(1);
        String sortField = USER_SORT_FIELD_MAP.getOrDefault(poleSortowania, "email");
        Page<UserRegistrationDto> usersPage = userService.findAllUsers(pageNumber, PAGE_SIZE, sortField, sortDir);
        int totalPages = usersPage.getTotalPages();
        List<UserRegistrationDto> users = usersPage.getContent();
        model.addAttribute("users", users);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("heading", "Lista użytkoników");
        model.addAttribute("sortField", poleSortowania);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("baseUrl", "/admin/list-uzytkownikow");
        return "admin/admin-user-list";
    }

    @GetMapping("/admin/uzytkownik/{userId}")
    public String getUserDetails(@PathVariable long userId,
                                 Model model){
        UserRegistrationDto user = userService.findUserById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        long commentCount = commentService.countUserComments(user.getEmail());
        long favouriteRecipesCount = recipeService.countFavouriteUserRecipes(user.getEmail());

        model.addAttribute("favouriteRecipesCount", favouriteRecipesCount);
        model.addAttribute("commentCount", commentCount);
        model.addAttribute("user", user);
        return "admin/admin-user-details";
    }

    @GetMapping("/admin/uzytkownik/{userId}/komentarze/{pageNo}")
    public String getUserComments(@PathVariable long userId,
                                  @PathVariable Optional<Integer> pageNo,
                                  Model model){
        int pageNumber = pageNo.orElse(1);
        UserRegistrationDto user = userService.findUserById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<CommentDto> allUserCommentsPages = commentService.findAllUserComments(user.getEmail(), pageNumber, PAGE_SIZE, COMMENT_SORT_FILED);
        int totalPages = allUserCommentsPages.getTotalPages();
        List<CommentDto> comments = allUserCommentsPages.getContent();
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("comments", comments);
        model.addAttribute("baseUrl", "/admin/uzytkownik/" + userId + "/komentarze");
        model.addAttribute("heading", "Komentarze użytkownika " + user.getNickName());
        return "admin/admin-user-comments";
    }


}
