package com.example.recipes.domain.comment;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentDateTimeProvider {
    LocalDateTime getCurrentTime(){
        return LocalDateTime.now();
    }
}
