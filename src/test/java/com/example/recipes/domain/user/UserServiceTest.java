package com.example.recipes.domain.user;

import com.example.recipes.domain.comment.CommentRepository;
import com.example.recipes.domain.rating.RatingRepository;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.user.dto.UserCredentialsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.Role;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepositoryMock;
    @Mock private UserRoleRepository userRoleRepositoryMock;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock RecipeRepository recipeRepositoryMock;
    @Mock CommentRepository commentRepositoryMock;
    @Mock RatingRepository ratingRepositoryMock;
    private UserService userService;

    @BeforeEach
    void init(){
        userService = new UserService(userRepositoryMock, userRoleRepositoryMock, passwordEncoder, recipeRepositoryMock, commentRepositoryMock, ratingRepositoryMock);
    }

    @Test
    void shouldReturnUserCredentialsDtoWhenEmailExists() {
        //given
        UserRole userRole = new UserRole();
        userRole.setId(11L);
        userRole.setName("USER");
        Set<UserRole> roles = new HashSet<>();
        roles.add(userRole);

        User user = new User();
        user.setEmail("example@mail.com");
        user.setRoles(roles);
        user.setPassword("hardpass");

        Mockito.when(userRepositoryMock.findByEmail("example@mail.com")).thenReturn(Optional.of(user));

        //when
        UserCredentialsDto userCredentialsDto = userService.findCredentialsByEmail("example@mail.com").orElseThrow();

        //then
        assertNotNull(userCredentialsDto);
        assertThat(userCredentialsDto.getEmail(), is("example@mail.com"));
        assertThat(userCredentialsDto.getRoles().size(), is(1));
        assertThat(userCredentialsDto.getRoles().size(), is(1));
        assertThat(userCredentialsDto.getPassword(), is("hardpass"));
    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist() {
        //given
        Mockito.when(userRepositoryMock.findByEmail("doesNotExist@mail.com")).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(NoSuchElementException.class, () -> userService.findCredentialsByEmail("doesNotExist@mail.com").orElseThrow());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        //when
        //then
        assertThrows(IllegalArgumentException.class, () -> userService.findCredentialsByEmail(null).orElseThrow());
    }

    @Test
    void registerUserWithDefaultRole() {
    }

    @Test
    void addOrUpdateFavoriteRecipe() {
    }

    @Test
    void favoritesCount() {
    }

    @Test
    void findUserById() {
    }

    @Test
    void findUserByName() {
    }

    @Test
    void findUserToUpdateById() {
    }

    @Test
    void updateUserData() {
    }

    @Test
    void updateUserPassword() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void findUsersCommentById() {
    }

    @Test
    void updateUsersComment() {
    }

    @Test
    void findAllUsers() {
    }
}