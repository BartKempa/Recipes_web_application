package com.example.recipes.domain.user;

import com.example.recipes.domain.user.dto.UserRegistrationDto;

class UserRegistrationDtoMapper {
    static UserRegistrationDto map(User user){
        return new UserRegistrationDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getNickName(),
                user.getAge()
        );
    }
}
