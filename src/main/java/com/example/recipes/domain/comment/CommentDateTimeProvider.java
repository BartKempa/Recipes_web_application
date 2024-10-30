package com.example.recipes.domain.comment;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentDateTimeProvider {
    LocalDateTime getCurrentTime(){
        return LocalDateTime.now();
    }
}
