package com.example.recipes.domain.user;

import com.example.recipes.domain.user.dto.UserRetrievePasswordDto;

public class UserRetrievePasswordDtoMapper {

    static UserRetrievePasswordDto map(User user){
        return new UserRetrievePasswordDto(
                user.getId(),
                user.getPassword(),
                user.getPasswordResetToken()
        );
    }
}
