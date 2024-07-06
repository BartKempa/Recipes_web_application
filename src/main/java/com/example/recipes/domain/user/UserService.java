package com.example.recipes.domain.user;

import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.user.dto.UserCredentialsDto;
import com.example.recipes.domain.user.dto.UserRegistrationDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {
    private static final String DEFAULT_USER_ROLE = "USER";
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecipeRepository recipeRepository;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder, RecipeRepository recipeRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.recipeRepository = recipeRepository;
    }

    public Optional<UserCredentialsDto> findCredentialsByEmail(String email){
        return userRepository.findByEmail(email)
                .map(UserCredentialsDtoMapper::map);
    }

    @Transactional
    public void registerUserWithDefaultRole(UserRegistrationDto userRegistrationDto){
        User user = new User();
        user.setEmail(userRegistrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setNickName(userRegistrationDto.getNickName());
        user.setAge(userRegistrationDto.getAge());
        UserRole userRole = userRoleRepository.findByName(DEFAULT_USER_ROLE).orElseThrow();
        user.getRoles().add(userRole);
        userRepository.save(user);
    }

    @Transactional
    public void addOrUpdateFavoriteRecipe(String userEmail, long recipeId){
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
        boolean isRecipeFavourite = user.getFavoriteRecipes().stream().anyMatch(r -> r.getId() == recipeId);
       if (user.getFavoriteRecipes().contains(recipe)){
           user.getFavoriteRecipes().remove(recipe);
       } else {
           user.getFavoriteRecipes().add(recipe);
       }
       userRepository.save(user);
    }

    public int favoritesCount(long recipeId){
         return userRepository.countUsersByFavouriteRecipe(recipeId);
    }

    public Optional<UserRegistrationDto> findUserById(long userId){
        return userRepository.findById(userId)
                .map(UserRegistrationDtoMapper::map);
    }

    public Optional<UserRegistrationDto> findUserByName(String userName){
        return userRepository.findByEmail(userName)
                .map(UserRegistrationDtoMapper::map);
    }

    @Transactional
    public void updateUserData(UserRegistrationDto userRegistrationDto) {
        User user = userRepository.findById(userRegistrationDto.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setNickName(userRegistrationDto.getNickName());
        user.setAge(userRegistrationDto.getAge());
        userRepository.save(user);
    }

    @Transactional
    public void updateUserPassword(UserRegistrationDto userRegistrationDto) {
        User user = userRepository.findById(userRegistrationDto.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        userRepository.save(user);
    }
}


