package com.example.recipes.web;

import com.example.recipes.domain.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
class FavouriteRecipeController {
    private final UserService userService;

    FavouriteRecipeController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/dodaj-do-ulubione")
    String addToFavouriteRecipes(@RequestParam long recipeId,
                                        @RequestHeader String referer,
                                        Authentication authentication){
        String currentUserEmail = authentication.getName();
        userService.addOrUpdateFavoriteRecipe(currentUserEmail, recipeId);
        return "redirect:" + referer;
    }
}
