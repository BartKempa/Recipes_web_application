package com.example.recipes.domain.config;

import com.example.recipes.domain.user.UserService;
import com.example.recipes.domain.user.dto.UserCredentialsDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserCredentialsDto userCredentialsDto = userService.findCredentialsByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Użytkownik o emailu %s nie został odnaleziony".formatted(username)));
        if (!userCredentialsDto.isEmailVerified()){
            throw new IllegalArgumentException("Twój email nie został potwierdzony w trakcie rejestracji.");
        }
        return createUserDetails(userCredentialsDto);
    }


    private UserDetails createUserDetails(UserCredentialsDto credentials) {
        return User.builder()
                .username(credentials.getEmail())
                .password(credentials.getPassword())
                .roles(credentials.getRoles().toArray(String[]::new))
                .build();
    }


     /*  @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.findCredentialsByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User with email %s not found".formatted(username)));
    }*/

}
