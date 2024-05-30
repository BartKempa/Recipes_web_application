package com.example.recipes.domain.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class CustomSecurityConfig {
    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String EDITOR_ROLE = "EDITOR";

    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(request -> request
                .requestMatchers("/admin/**").hasAnyRole(ADMIN_ROLE, EDITOR_ROLE)
                .anyRequest().permitAll()
        )
                .formLogin(Customizer.withDefaults())
                .build();
    }


}
