package com.example.recipes.web.admin;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class AdminController {
    static final String ADMIN_NOTIFICATION_ATTRIBUTE = "adminNotification";

    @GetMapping("/admin")
    String getAdminPanel(){
        return "admin/admin";
    }

}
