package com.example.recipes.domain.user;

import com.example.recipes.domain.user.dto.UserCredentialsDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserCredentialsDto> findCredentialsByEmail(String email){
        return userRepository.findByEmail(email)
                .map(UserCredentialsDtoMapper::map);
    }
}
