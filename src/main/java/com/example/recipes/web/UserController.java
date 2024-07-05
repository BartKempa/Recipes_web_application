package com.example.recipes.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/uzytkownik")
    public String getUserPanel(){
        return "user-panel";
    }
}
