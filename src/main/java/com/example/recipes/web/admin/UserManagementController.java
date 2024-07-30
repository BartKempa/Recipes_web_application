package com.example.recipes.web.admin;

import com.example.recipes.domain.user.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class UserManagementController {
    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }


}
