package com.example.recipes.domain.user;

import com.example.recipes.domain.user.dto.UserCredentialsDto;

import java.util.stream.Collectors;

class UserCredentialsDtoMapper {
  static UserCredentialsDto map(User user){
   return new UserCredentialsDto(
           user.getEmail(),
           user.getPassword(),
           user.isEmailVerified(),
           user.getRoles()
                   .stream()
                   .map(UserRole::getName)
                   .collect(Collectors.toSet())
   );
  }
}
