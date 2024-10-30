package com.example.recipes.domain.user;

import com.example.recipes.domain.user.dto.UserUpdateDto;

class UserUpdateDtoMapper {
    static UserUpdateDto map(User user){
        return new UserUpdateDto(
                user.getId(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getNickName(),
                user.getAge()
        );
    }
}
