package com.example.recipes.web;

import com.example.recipes.domain.rating.RatingService;
import org.springframework.stereotype.Controller;

@Controller
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }
}
