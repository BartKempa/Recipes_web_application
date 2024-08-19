package com.example.recipes.domain.comment;

import java.time.LocalDateTime;

class CommentDateTimeProvider {
    LocalDateTime getCurrentTime(){
        return LocalDateTime.now();
    }
}
