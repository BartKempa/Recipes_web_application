package com.example.recipes.web;

import com.example.recipes.domain.rating.RatingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
class RatingController {
    private final RatingService ratingService;

    RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/ocen-przepis")
    String addRecipeRating(@RequestParam long recipeId,
                                  @RequestParam int rating,
                                  @RequestHeader String referer,
                                  Authentication authentication){
        String currentUserEmail = authentication.getName();
        ratingService.addOrUpdateRating(currentUserEmail, recipeId, rating);
        return "redirect:" + referer;
    }
}
