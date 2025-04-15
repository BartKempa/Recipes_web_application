package com.example.recipes.web;

import com.example.recipes.domain.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ForgotPasswordController {
    private final static String USER_NOTIFICATION_ATTRIBUTE = "userNotification";

    private final UserService userService;

    public ForgotPasswordController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/odzyskiwanie-hasla")
    String getRetrievePasswordForm(){
        return "retrieve-password-form";
    }

    @PostMapping("/odzyskiwanie-hasla")
    String retrievePassword(
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes,
            @RequestHeader String referer){
        boolean isEmailExists = userService.checkEmailExists(email);
        if (isEmailExists){
            userService.sendResetLink(email);
            redirectAttributes.addFlashAttribute(
                    USER_NOTIFICATION_ATTRIBUTE,
                    "Link do zmiany hasła został wysłany na adres %s".formatted(email)
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    USER_NOTIFICATION_ATTRIBUTE,
                    "Niestety konto z podanym mailem nie zostało znalezione. Upewnij się, że podałeś prawidłowy adres email."
            );
        }
        return "redirect:" + referer;
    }
}
