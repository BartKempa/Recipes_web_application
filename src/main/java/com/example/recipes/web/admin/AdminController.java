package com.example.recipes.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class AdminController {
    static final String ADMIN_NOTIFICATION_ATTRIBUTE = "adminNotification";

    @GetMapping("/admin")
    String getAdminPanel(){
        return "admin/admin";
    }
}
