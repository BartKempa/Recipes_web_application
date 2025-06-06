package com.example.recipes.web;

import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserService;
import com.example.recipes.domain.user.dto.UserRetrievePasswordDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ForgotPasswordController {
    private final static String USER_NOTIFICATION_ATTRIBUTE = "userNotification";

    private final UserService userService;

    public ForgotPasswordController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/odzyskiwanie-hasla")
    String getLinkForm(){
        return "get-link-to-retrieve-password-form";
    }

    @PostMapping("/odzyskiwanie-hasla")
    String getLinkToRetrievePassword(@RequestParam("email") String email,
                                     RedirectAttributes redirectAttributes,
                                     @RequestHeader(required = false) String referer) {
        Optional<User> optionalUser = userService.findUserByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.isEmailVerified()) {
                redirectAttributes.addFlashAttribute(
                        USER_NOTIFICATION_ATTRIBUTE,
                        "Twoje konto nie zostało jeszcze aktywowane. Sprawdź skrzynkę e-mail i kliknij w link aktywacyjny."
                );
            } else {
                userService.sendResetLink(email);
                redirectAttributes.addFlashAttribute(
                        USER_NOTIFICATION_ATTRIBUTE,
                        "Link do zmiany hasła został wysłany na adres %s, link będzie ważny 5 minut".formatted(email)
                );
            }
        } else {
            redirectAttributes.addFlashAttribute(
                    USER_NOTIFICATION_ATTRIBUTE,
                    "Konto z podanym mailem nie zostało znalezione. Upewnij się, że podałeś prawidłowy adres email."
            );
        }

        return "redirect:" + (referer != null ? referer : "/odzyskiwanie-hasla");
    }

    @GetMapping("/reset-hasla")
    String getRetrievePasswordForm(@RequestParam String token,
                                   RedirectAttributes redirectAttributes,
                                   Model model){
        if (!userService.checkResetTokenExists(token)){
            redirectAttributes.addFlashAttribute(
                    USER_NOTIFICATION_ATTRIBUTE,
                    "Token do zmiany hasła jest nieważny. Wyśli ponownie link do zmiany hasła."
            );
            return "redirect:/odzyskiwanie-hasla";
        } else if (!userService.checkResetTokenNotExpired(token)) {
            redirectAttributes.addFlashAttribute(
                    USER_NOTIFICATION_ATTRIBUTE,
                    "Token do zmiany hasła jest nieaktualny, upłynał termin jego ważności."
            );
            return "redirect:/odzyskiwanie-hasla";
        } else {
            UserRetrievePasswordDto user = userService.findUserToRetrievePasswordByToken(token)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            model.addAttribute("user", user);
            return "retrieve-password-form";
        }
    }

    @PostMapping("/reset-hasla")
    String retrievePassword(@Valid @ModelAttribute("user") UserRetrievePasswordDto user,
                            BindingResult bindingResult,
                            @RequestParam("confirmPassword") String confirmPassword,
                            RedirectAttributes redirectAttributes,
                            Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("user", user);
            return "retrieve-password-form";
        } else if (!user.getPassword().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute(
                    USER_NOTIFICATION_ATTRIBUTE,
                    "Hasła nie są jednakowe! Spróbuj ponownie."
            );
            return "redirect:/reset-hasla?token=" + user.getToken();
        } else {
            userService.retrieveUserPassword(user);
            redirectAttributes.addFlashAttribute(
                    USER_NOTIFICATION_ATTRIBUTE,
                    "Hasło zostało pomyślnie zmienione, zaloguj się wykorzystując nowe hasło!"
            );
            return "redirect:/login";
        }
    }
}
