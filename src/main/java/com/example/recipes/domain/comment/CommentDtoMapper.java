package com.example.recipes.domain.comment;

import com.example.recipes.domain.comment.dto.CommentDto;

class CommentDtoMapper {
    static CommentDto map(Comment comment){
        return new CommentDto(
                comment.getId(),
                comment.isApproved(),
                comment.getText(),
                comment.getCreationDate(),
                comment.getUser().getEmail()
        );
    }
}
