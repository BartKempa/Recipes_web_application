package com.example.recipes.web;

import com.example.recipes.domain.user.UserService;
import com.example.recipes.domain.user.dto.UserRegistrationDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/uzytkownik")
    public String getUserPanel(Model model, Authentication authentication){
        String currentUserEmail = authentication.getName();
        UserRegistrationDto user = userService.findUserByName(currentUserEmail).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("user", user);
        return "user-panel";
    }

    @GetMapping("/uzytkownik/aktualizacja/{userId}")
    public String getUpdateUserForm(@PathVariable(value = "userId") long userId, Model model){
        UserRegistrationDto user = userService.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("user", user);
        return "user-update-form";
    }

    @PostMapping("/uzytkownik/aktualizacja")
    public String updateUser(UserRegistrationDto user){
        userService.updateUser(user);
        return "redirect:/";
    }
}
