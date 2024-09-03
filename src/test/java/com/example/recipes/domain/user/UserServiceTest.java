package com.example.recipes.domain.user;

import com.example.recipes.domain.comment.CommentRepository;
import com.example.recipes.domain.rating.RatingRepository;
import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.user.dto.UserCredentialsDto;
import com.example.recipes.domain.user.dto.UserRegistrationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Mock private PasswordEncoder passwordEncoderMock;
    @Mock private RecipeRepository recipeRepositoryMock;
    @Mock private CommentRepository commentRepositoryMock;
    @Mock private RatingRepository ratingRepositoryMock;
    private UserService userService;

    @BeforeEach
    void init(){
        userService = new UserService(userRepositoryMock, userRoleRepositoryMock, passwordEncoderMock, recipeRepositoryMock, commentRepositoryMock, ratingRepositoryMock);
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
        //given
        //when
        //then
        assertThrows(IllegalArgumentException.class, () -> userService.findCredentialsByEmail(null).orElseThrow());
    }

    @Test
    void shouldRegisterNewUserWithDefaultRole() {
        //given
        final String USER_EMAIL = "example@mail.com";
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail(USER_EMAIL);
        userRegistrationDto.setPassword("hardpass");
        userRegistrationDto.setFirstName("Bartek");
        userRegistrationDto.setLastName("Kowalski");
        userRegistrationDto.setNickName("Barti");
        userRegistrationDto.setAge(40);

        UserRole userRole = new UserRole();
        userRole.setId(21L);
        userRole.setName("USER");

        Mockito.when(userRoleRepositoryMock.findByName("USER")).thenReturn(Optional.of(userRole));
        Mockito.when(passwordEncoderMock.encode(userRegistrationDto.getPassword())).thenReturn("encodedHardpass");

        //when
        userService.registerUserWithDefaultRole(userRegistrationDto);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepositoryMock).save(userArgumentCaptor.capture());

        User userCaptorValue = userArgumentCaptor.getValue();
        assertThat(userCaptorValue.getEmail(), is(USER_EMAIL));
        assertThat(userCaptorValue.getPassword(), is("encodedHardpass"));
        assertThat(userCaptorValue.getFirstName(), is("Bartek"));
        assertThat(userCaptorValue.getLastName(), is("Kowalski"));
        assertThat(userCaptorValue.getNickName(), is("Barti"));
        assertThat(userCaptorValue.getAge(), is(40));
        assertThat(userCaptorValue.getRoles().size(), is(1));
        assertThat(userCaptorValue.getRoles().iterator().next().getName(), is("USER"));
    }

    @Test
    void shouldAddRecipeToFavouritesWhenNotAlreadyPresent() {
        //given
        final long RECIPE_ID = 1L;
        final String USER_EMAIL = "example@mail.com";

        UserRole userRole = new UserRole();
        userRole.setId(11L);
        userRole.setName("USER");

        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setRoles(Set.of(userRole));
        user.setPassword("hardpass");

        Recipe recipe = new Recipe();
        recipe.setId(RECIPE_ID);
        recipe.setName("Buraczkowa");

        Mockito.when(userRepositoryMock.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        Mockito.when(recipeRepositoryMock.findById(RECIPE_ID)).thenReturn(Optional.of(recipe));

        //before add
        assertThat(user.getFavoriteRecipes().size(), is(0));

        //when
        userService.addOrUpdateFavoriteRecipe(USER_EMAIL, RECIPE_ID);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepositoryMock).save(userArgumentCaptor.capture());

        User userCaptorValue = userArgumentCaptor.getValue();
        assertThat(userCaptorValue.getFavoriteRecipes().size(), is(1));
        assertThat(userCaptorValue.getFavoriteRecipes().iterator().next().getId(), is(RECIPE_ID));
    }

    @Test
    void shouldRemoveRecipeFromFavouritesWhenAlreadyPresent() {
        //given
        final long RECIPE_ID = 1L;
        final String USER_EMAIL = "example@mail.com";

        UserRole userRole = new UserRole();
        userRole.setId(11L);
        userRole.setName("USER");

        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setRoles(Set.of(userRole));
        user.setPassword("hardpass");

        Recipe recipe = new Recipe();
        recipe.setId(RECIPE_ID);
        recipe.setName("Buraczkowa");
        user.setFavoriteRecipes(Set.of(recipe));

        Mockito.when(userRepositoryMock.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        Mockito.when(recipeRepositoryMock.findById(RECIPE_ID)).thenReturn(Optional.of(recipe));

        //before remove
        assertThat(user.getFavoriteRecipes().size(), is(1));
        assertThat(user.getFavoriteRecipes().iterator().next().getId(), is(RECIPE_ID));

        //when
        userService.addOrUpdateFavoriteRecipe(USER_EMAIL, RECIPE_ID);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepositoryMock).save(userArgumentCaptor.capture());

        User userCaptorValue = userArgumentCaptor.getValue();
        assertThat(userCaptorValue.getFavoriteRecipes().size(), is(0));
    }

    @Test
    void shouldReturnTwoWhenRecipeIsFavoritedByTwoUsers() {
        //given
        final long RECIPE_ID = 1L;
        Mockito.when(userRepositoryMock.countUsersByFavouriteRecipe(RECIPE_ID)).thenReturn(2);

        //when
        int favoritesCount = userService.favoritesCount(RECIPE_ID);

        //then
        assertThat(favoritesCount, is(2));
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