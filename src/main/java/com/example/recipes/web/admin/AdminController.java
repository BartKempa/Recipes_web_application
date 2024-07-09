package com.example.recipes.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    public static final String ADMIN_NOTIFICATION_ATTRIBUTE = "adminNotification";

    @GetMapping("/admin")
    public String getAdminPanel(){

        return "admin/admin";
    }
}
