package com.example.recipes.web.admin;

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
    private final static int PAGE_SIZE = 6;
    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
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

        model.addAttribute("user", user);
        return "admin/admin-user-details";

    }





}
