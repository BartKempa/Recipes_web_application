package com.example.recipes.domain.recipe;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RecipeDateTimeProvider {

    LocalDateTime getCurrentTime(){
        return LocalDateTime.now();
    }
}
