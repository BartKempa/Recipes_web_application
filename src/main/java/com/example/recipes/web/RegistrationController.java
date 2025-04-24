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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
class RegistrationController {
    private final UserService userService;
    private final static String USER_NOTIFICATION_ATTRIBUTE = "userNotification";

    RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/rejestracja")
    String registerForm(Model model){
        UserRegistrationDto user = new UserRegistrationDto();
        model.addAttribute("user", user);
        return "registration-form";
    }

    @PostMapping("/rejestracja")
    String register(@Valid @ModelAttribute("user") UserRegistrationDto user,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("user", user);
            return "registration-form";
        } else {
            userService.registerUserWithDefaultRole(user);
            redirectAttributes.addFlashAttribute(
                    USER_NOTIFICATION_ATTRIBUTE,
                    "Na Twoją skrzynkę mialową została wysłana wiadomość z linkiem aktywacyjnym, który będzie ważny przez 12 godzin."
            );
            return "redirect:/login";
        }
    }

    @GetMapping("/aktywacja-konta")
    String activateAccount(@RequestParam String token,
                           RedirectAttributes redirectAttributes){
        if (!userService.checkActivationTokenExists(token)){
            redirectAttributes.addFlashAttribute(
                    USER_NOTIFICATION_ATTRIBUTE,
                    "Token do aktywacji konta jest nieważny."
            );
            return "redirect:/login";
        } else if (!userService.checkActivationTokenNotExpired(token)) {
            redirectAttributes.addFlashAttribute(
                    USER_NOTIFICATION_ATTRIBUTE,
                    "Token do aktywacji konta jest nieaktualny, upłynał termin jego ważności."
            );
            return "redirect:/login";
        } else {
            userService.activateAccount(token);
            redirectAttributes.addFlashAttribute(
                    USER_NOTIFICATION_ATTRIBUTE,
                    "Konto zostało pomyślnie aktywowane, możesz się zalogować!"
            );
            return "redirect:/login";
        }
    }
}
