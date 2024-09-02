package com.example.recipes.web;

import com.example.recipes.domain.user.UserService;
import com.example.recipes.domain.user.dto.UserRegistrationDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/rejestracja")
    public String registerForm(Model model){
        UserRegistrationDto user = new UserRegistrationDto();
        model.addAttribute("user", user);
        return "registration-form";
    }

    @PostMapping("/rejestracja")
    public String register(@Valid @ModelAttribute("user") UserRegistrationDto user,
                           BindingResult bindingResult,
                           Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("user", user);
            return "registration-form";
        } else {
            userService.registerUserWithDefaultRole(user);
            return "redirect:/";
        }
    }
}
