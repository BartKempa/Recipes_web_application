package com.example.recipes.domain.user;

import com.example.recipes.domain.comment.CommentRepository;
import com.example.recipes.domain.rating.RatingRepository;
import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.user.dto.UserCredentialsDto;
import com.example.recipes.domain.user.dto.UserRegistrationDto;
import com.example.recipes.domain.user.dto.UserRetrievePasswordDto;
import com.example.recipes.domain.user.dto.UserUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private UserRoleRepository userRoleRepositoryMock;
    @Mock
    private PasswordEncoder passwordEncoderMock;
    @Mock
    private RecipeRepository recipeRepositoryMock;
    @Mock
    private CommentRepository commentRepositoryMock;
    @Mock
    private RatingRepository ratingRepositoryMock;
    @Mock
    private JavaMailSender javaMailSender;
    private UserService userService;

    @BeforeEach
    void init() {
        userService = new UserService(userRepositoryMock, userRoleRepositoryMock, passwordEncoderMock, recipeRepositoryMock, commentRepositoryMock, ratingRepositoryMock, javaMailSender);
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
        final String token = "activation-token";
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
        UserService spyService = Mockito.spy(userService);
        Mockito.doReturn(token).when(spyService).generateToken();

        //when
        spyService.registerUserWithDefaultRole(userRegistrationDto);

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

        ArgumentCaptor<SimpleMailMessage> messageArgumentCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(javaMailSender).send(messageArgumentCaptor.capture());

        SimpleMailMessage mailMessage = messageArgumentCaptor.getValue();
        assertThat(mailMessage.getFrom()).isEqualTo("kuchniabartosza@gmail.com");
        assertThat(mailMessage.getTo()).contains(USER_EMAIL);
        assertThat(mailMessage.getSubject()).isEqualTo("Aktywacja nowego konta");
        assertThat(mailMessage.getText()).contains(token);
        assertThat(mailMessage.getText()).contains("http://localhost:8080/aktywacja-konta?token=activation-token");
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
        user.setFavoriteRecipes(new HashSet<>());
        user.getFavoriteRecipes().add(recipe);

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
    void shouldReturnZeroWhenRecipeIsNotFavoritedByAnyUser() {
        //given
        final long RECIPE_ID = 111L;
        Mockito.when(userRepositoryMock.countUsersByFavouriteRecipe(RECIPE_ID)).thenReturn(0);

        //when
        int favoritesCount = userService.favoritesCount(RECIPE_ID);

        //then
        assertThat(favoritesCount, is(0));
    }

    @Test
    void shouldReturnUserRegistrationDtoWhenLookingByIdAndUserExists() {
        // given
        final long USER_ID = 11L;
        final String USER_EMAIL = "example@mail.com";

        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setFirstName("Bartek");
        user.setLastName("Kowalski");
        user.setNickName("Barti");

        Mockito.when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.of(user));

        // when
        UserRegistrationDto userRegistrationDto = userService.findUserById(USER_ID).orElseThrow();

        // then
        assertThat(userRegistrationDto.getEmail(), is(USER_EMAIL));
        assertThat(userRegistrationDto.getFirstName(), is("Bartek"));
        assertThat(userRegistrationDto.getLastName(), is("Kowalski"));
        assertThat(userRegistrationDto.getNickName(), is("Barti"));
    }

    @Test
    void shouldThrowExceptionWhenLookingByIdAndUserNotExists() {
        // given
        final long USER_ID = 11L;

        Mockito.when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(NoSuchElementException.class, () -> userService.findUserById(USER_ID).orElseThrow());
    }

    @Test
    void shouldReturnUserRegistrationDtoWhenLookingByNameAndUserExists() {
        // given
        final String USER_EMAIL = "example@mail.com";

        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setFirstName("Bartek");
        user.setLastName("Kowalski");
        user.setNickName("Barti");

        Mockito.when(userRepositoryMock.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        // when
        UserRegistrationDto userRegistrationDto = userService.findUserByName(USER_EMAIL).orElseThrow();

        // then
        assertThat(userRegistrationDto.getEmail(), is(USER_EMAIL));
        assertThat(userRegistrationDto.getFirstName(), is("Bartek"));
        assertThat(userRegistrationDto.getLastName(), is("Kowalski"));
        assertThat(userRegistrationDto.getNickName(), is("Barti"));
    }

    @Test
    void shouldThrowExceptionWhenLookingByNameAndUserNotExists() {
        // given
        final String USER_EMAIL = "example@mail.com";
        Mockito.when(userRepositoryMock.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(NoSuchElementException.class, () -> userService.findUserByName(USER_EMAIL).orElseThrow());
    }

    @Test
    void shouldReturnUserUpdateDtoWhenLookingByIdAndUserExists() {
        // given
        final long USER_ID = 11L;

        User user = new User();
        user.setAge(40);
        user.setFirstName("Bartek");
        user.setLastName("Kowalski");
        user.setNickName("Barti");

        Mockito.when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.of(user));

        // when
        UserUpdateDto userUpdateDto = userService.findUserToUpdateById(USER_ID).orElseThrow();

        // then
        assertThat(userUpdateDto.getAge(), is(40));
        assertThat(userUpdateDto.getFirstName(), is("Bartek"));
        assertThat(userUpdateDto.getLastName(), is("Kowalski"));
        assertThat(userUpdateDto.getNickName(), is("Barti"));
    }

    @Test
    void shouldThrowExceptionWhenLookingUserUpdateDtoByIdAndUserNotExists() {
        // given
        final long USER_ID = 11L;

        Mockito.when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(NoSuchElementException.class, () -> userService.findUserToUpdateById(USER_ID).orElseThrow());
    }

    @Test
    void shouldUpdateUserDataWhenTryChangeIt() {
        //given
        final long USER_ID = 11L;
        User user = createUser();

        UserUpdateDto userUpdateDto = createUserUpdateDto(USER_ID);

        Mockito.when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.of(user));

        //when
        userService.updateUserData(userUpdateDto);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepositoryMock).save(userArgumentCaptor.capture());

        User argumentCaptorValue = userArgumentCaptor.getValue();
        assertThat(argumentCaptorValue.getFirstName(), is("Pawel"));
        assertThat(argumentCaptorValue.getLastName(), is("Mickiewicz"));
        assertThat(argumentCaptorValue.getNickName(), is("Pita"));
        assertThat(argumentCaptorValue.getAge(), is(38));
    }

    private static UserUpdateDto createUserUpdateDto(long USER_ID) {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId(USER_ID);
        userUpdateDto.setFirstName("Pawel");
        userUpdateDto.setLastName("Mickiewicz");
        userUpdateDto.setNickName("Pita");
        userUpdateDto.setAge(38);
        return userUpdateDto;
    }

    private static User createUser() {
        User user = new User();
        user.setAge(40);
        user.setFirstName("Bartek");
        user.setLastName("Kowalski");
        user.setNickName("Barti");
        user.setPassword("hardpass");
        return user;
    }

    @Test
    void shouldUpdateUserDataWhenTryChangePassword() {
        //given
        final long USER_ID = 11L;
        User user = createUser();

        UserUpdateDto userUpdateDto = createUserUpdateDto(USER_ID);
        userUpdateDto.setPassword("newpass");

        Mockito.when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoderMock.encode(userUpdateDto.getPassword())).thenReturn("encodedNewPass");

        //when
        userService.updateUserPassword(userUpdateDto);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepositoryMock).save(userArgumentCaptor.capture());

        User updatedUser = userArgumentCaptor.getValue();
        assertThat(updatedUser.getPassword(), is("encodedNewPass"));
        Mockito.verify(passwordEncoderMock, Mockito.times(1)).encode("newpass");
    }

    @Test
    void shouldFindAllUsersSortedByFirstNameAsc() {
        User user1 = new User();
        user1.setId(1L);
        user1.setAge(40);
        user1.setFirstName("Bartek");
        user1.setLastName("Kowalski");
        user1.setNickName("Barti");

        User user2 = new User();
        user2.setId(1L);
        user2.setAge(30);
        user2.setFirstName("Darek");
        user2.setLastName("Darkowski");
        user2.setNickName("Darunia");

        List<User> userList = List.of(user2, user1);

        Mockito.when(userRepositoryMock.findAll(Mockito.any(Pageable.class))).thenAnswer(invocationOnMock -> {
            Pageable pageable = invocationOnMock.getArgument(0);
            List<User> sortedList = userList.stream().sorted((u1, u2) -> {
                if (pageable.getSort().getOrderFor("firstName") != null && pageable.getSort().getOrderFor("firstName").isAscending()) {
                    return u1.getFirstName().compareTo(u2.getFirstName());
                } else {
                    return u2.getFirstName().compareTo(u1.getFirstName());
                }
            }).toList();
            return new PageImpl<>(sortedList, pageable, sortedList.size());
        });
        int pageNumber = 1;
        int pageSize = 3;
        String sortField = "firstName";
        String sortDirection = "asc";

        //when
        Page<UserRegistrationDto> paginatedUserList = userService.findAllUsers(pageNumber, pageSize, sortField, sortDirection);

        //then
        assertThat(paginatedUserList.getTotalElements(), is(2L));
        assertThat(paginatedUserList.getContent().get(0).getFirstName(), is("Bartek"));
        assertThat(paginatedUserList.getContent().get(1).getFirstName(), is("Darek"));
    }

    @Test
    void shouldFindAllUsersSortedByAgeDesc() {
        User user1 = new User();
        user1.setId(1L);
        user1.setAge(40);
        user1.setFirstName("Bartek");
        user1.setLastName("Kowalski");
        user1.setNickName("Barti");

        User user2 = new User();
        user2.setId(1L);
        user2.setAge(30);
        user2.setFirstName("Darek");
        user2.setLastName("Darkowski");
        user2.setNickName("Darunia");

        List<User> userList = List.of(user1, user2);

        Mockito.when(userRepositoryMock.findAll(Mockito.any(Pageable.class))).thenAnswer(invocationOnMock -> {
            Pageable pageable = invocationOnMock.getArgument(0);
            List<User> sortedList = userList.stream().sorted((u1, u2) -> {
                if (pageable.getSort().getOrderFor("age") != null && pageable.getSort().getOrderFor("age").isAscending()) {
                    return Integer.compare(u1.getAge(), u2.getAge());
                } else {
                    return Integer.compare(u2.getAge(), u1.getAge());
                }
            }).toList();
            return new PageImpl<>(sortedList, pageable, sortedList.size());
        });
        int pageNumber = 1;
        int pageSize = 3;
        String sortField = "age";
        String sortDirection = "desc";

        //when
        Page<UserRegistrationDto> paginatedUserList = userService.findAllUsers(pageNumber, pageSize, sortField, sortDirection);

        //then
        assertThat(paginatedUserList.getTotalElements(), is(2L));
        assertThat(paginatedUserList.getContent().get(0).getFirstName(), is("Bartek"));
        assertThat(paginatedUserList.getContent().get(1).getFirstName(), is("Darek"));
    }

    @Test
    void shouldDeleteUserAndAssociatedCommentsRatingsRolesAndFavourites() {
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

        Set<Recipe> favoriteRecipes = new HashSet<>();
        favoriteRecipes.add(recipe);
        user.setFavoriteRecipes(favoriteRecipes);

        Set<UserRole> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        Mockito.when(userRepositoryMock.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        //when
        userService.deleteUser(USER_EMAIL);

        //then
        Mockito.verify(commentRepositoryMock, Mockito.times(1)).deleteAll(Mockito.anySet());
        Mockito.verify(ratingRepositoryMock, Mockito.times(1)).deleteAll(Mockito.anySet());
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findByEmail(USER_EMAIL);
        Mockito.verify(userRepositoryMock).delete(user);
        assertTrue(favoriteRecipes.isEmpty());
        assertTrue(roles.isEmpty());
    }

    @Test
    void shouldDeleteUserWithoutCommentsRatingsFavoritesAndRoles() {
        //given
        final String USER_EMAIL = "example@mail.com";
        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setRoles(new HashSet<>());
        user.setFavoriteRecipes(new HashSet<>());

        Mockito.when(userRepositoryMock.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        //when
        userService.deleteUser(USER_EMAIL);

        //then
        Mockito.verify(commentRepositoryMock, Mockito.times(1)).deleteAll(Mockito.anySet());
        Mockito.verify(ratingRepositoryMock, Mockito.times(1)).deleteAll(Mockito.anySet());
        Mockito.verify(userRepositoryMock).delete(user);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        //given
        final String USER_EMAIL = "example@mail.com";
        Mockito.when(userRepositoryMock.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(ResponseStatusException.class, () -> userService.deleteUser(USER_EMAIL));
    }

    @Test
    void shouldSendResetLink() {
        //given
        final String email = "example@mail.com";
        final String token = "fixed-token";

        UserRole userRole = new UserRole();
        userRole.setId(11L);
        userRole.setName("USER");

        User user = new User();
        user.setEmail(email);
        user.setRoles(Set.of(userRole));
        user.setPassword("hardpass");


        Mockito.when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(user));
        UserService spyService = Mockito.spy(userService);
        Mockito.doReturn(token).when(spyService).generateToken();

        //when
        spyService.sendResetLink(email);

        //then
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(javaMailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertThat(sentMessage.getFrom()).isEqualTo("kuchniabartosza@gmail.com");
        assertThat(sentMessage.getTo()).contains(email);
        assertThat(sentMessage.getSubject()).isEqualTo("Reset hasła");
        assertThat(sentMessage.getText()).contains(token);
        assertThat(sentMessage.getText()).contains("http://localhost:8080/reset-hasla?token=fixed-token");

    }

    @Test
    void shouldThrowExceptionWhenEmailDoseNotExist(){
        //given
        Mockito.when(userRepositoryMock.findByEmail("doesNotExist@mail.com")).thenReturn(Optional.empty());

        //when & then
        assertThrows(ResponseStatusException.class, () -> userService.sendResetLink("doesNotExist@mail.com"));
    }

    @Test
    void shouldSetTokenAndExpiryDateOnUser() {
        //given
        final String email = "example@mail.com";
        final String fixedToken = "fixed-token";

        User user = new User();
        user.setEmail(email);
        Mockito.when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(user));

        UserService spyService = Mockito.spy(userService);
        Mockito.doReturn(fixedToken).when(spyService).generateToken();

        //when
        spyService.sendResetLink(email);

        //then
        assertThat(user.getPasswordResetToken()).isEqualTo(fixedToken);
        assertThat(user.getPasswordResetTokenExpiry()).isAfter(LocalDateTime.now());
    }

    @Test
    void shouldReturnTrueWhenCheckResetTokenExists() {
        //given
        final String email = "example@mail.com";
        final String token = "fixed-token";

        UserRole userRole = new UserRole();
        userRole.setId(11L);
        userRole.setName("USER");

        User user = new User();
        user.setPasswordResetToken(token);
        user.setEmail(email);
        user.setRoles(Set.of(userRole));
        user.setPassword("hardpass");

        Mockito.when(userRepositoryMock.findByPasswordResetToken("fixed-token")).thenReturn(Optional.of(user));

        //when
        boolean checkResetTokenExists = userService.checkResetTokenExists(token);

        //then
        assertTrue(checkResetTokenExists);
    }

    @Test
    void shouldReturnFalseWhenCheckResetTokenExists() {
        //given
        final String token = "non-existing-token";
        Mockito.when(userRepositoryMock.findByPasswordResetToken("non-existing-token")).thenReturn(Optional.empty());

        //when
        boolean checkResetTokenExists = userService.checkResetTokenExists(token);

        //then
        assertFalse(checkResetTokenExists);
    }

    @Test
    void shouldReturnTrueWhenCheckResetTokenNotExpired() {
        //given
        String token = "valid-token";
        User user = new User();
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
        Mockito.when(userRepositoryMock.findByPasswordResetToken(token)).thenReturn(Optional.of(user));

        //when
        boolean result = userService.checkResetTokenNotExpired(token);

        //then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenCheckResetTokenNotExpired() {
        //given
        final String token = "fixed-token";
        User user = new User();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().minusMinutes(10));
        user.setPassword("hardpass");

        Mockito.when(userRepositoryMock.findByPasswordResetToken(token)).thenReturn(Optional.of(user));

        //when
        boolean isNotExpired = userService.checkResetTokenNotExpired(token);

        //then
        assertFalse(isNotExpired);
    }

    @Test
    void shouldReturnFalseWhenCheckNotExistingResetTokenNotExpired() {
        //given
        final String token = "non-existing-token";
        Mockito.when(userRepositoryMock.findByPasswordResetToken(token)).thenReturn(Optional.empty());

        //when & then
        assertThrows(ResponseStatusException.class, () -> userService.checkResetTokenNotExpired(token));
    }

    @Test
    void shouldRetrieveUserPassword() {
        //given
        final String email = "example@mail.com";
        final String token = "fixed-token";
        final String password = "Password123#";

        UserRetrievePasswordDto userRetrievePasswordDto = new UserRetrievePasswordDto(1L, password, token);
        UserRole userRole = new UserRole();
        userRole.setId(1L);
        userRole.setName("USER");

        User user = new User();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
        user.setEmail(email);
        user.setRoles(Set.of(userRole));
        user.setPassword("OldPass123#");

        Mockito.when(userRepositoryMock.findById(userRetrievePasswordDto.getId())).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoderMock.encode(userRetrievePasswordDto.getPassword())).thenReturn("Password123#");
        //when
        userService.retrieveUserPassword(userRetrievePasswordDto);

        //then
        assertNull(user.getPasswordResetToken());
        assertNull(user.getPasswordResetTokenExpiry());
        assertEquals("Password123#", user.getPassword());
    }

    @Test
    void shouldReturnTrueWhenCheckActiveTokenExists() {
        //given
        final String email = "example@mail.com";
        final String token = "activation-token";

        UserRole userRole = new UserRole();
        userRole.setId(11L);
        userRole.setName("USER");

        User user = new User();
        user.setEmailverificationtoken(token);
        user.setEmail(email);
        user.setRoles(Set.of(userRole));

        Mockito.when(userRepositoryMock.findByEmailVerificationToken("activation-token")).thenReturn(Optional.of(user));

        //when
        boolean checkActivationTokenExists = userService.checkActivationTokenExists(token);

        //then
        assertTrue(checkActivationTokenExists);
    }

    @Test
    void shouldReturnFalseWhenActivationTokenExists() {
        //given
        final String token = "non-existing-token";
        Mockito.when(userRepositoryMock.findByEmailVerificationToken("non-existing-token")).thenReturn(Optional.empty());

        //when
        boolean checkActivationTokenExists = userService.checkActivationTokenExists(token);

        //then
        assertFalse(checkActivationTokenExists);
    }

    @Test
    void shouldReturnTrueWhenActivationTokenNotExpired() {
        //given
        String token = "valid-token";
        User user = new User();
        user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusMinutes(10));
        Mockito.when(userRepositoryMock.findByEmailVerificationToken(token)).thenReturn(Optional.of(user));

        //when
        boolean result = userService.checkActivationTokenNotExpired(token);

        //then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenCheckActivationTokenNotExpired() {
        //given
        final String token = "fixed-token";
        User user = new User();
        user.setEmailverificationtoken(token);
        user.setEmailVerificationTokenExpiry(LocalDateTime.now().minusMinutes(10));

        Mockito.when(userRepositoryMock.findByEmailVerificationToken(token)).thenReturn(Optional.of(user));

        //when
        boolean isNotExpired = userService.checkActivationTokenNotExpired(token);

        //then
        assertFalse(isNotExpired);
    }

    @Test
    void shouldReturnFalseWhenCheckNotExistingActivationTokenNotExpired() {
        //given
        final String token = "non-existing-token";
        Mockito.when(userRepositoryMock.findByEmailVerificationToken(token)).thenReturn(Optional.empty());

        //when & then
        assertThrows(ResponseStatusException.class, () -> userService.checkActivationTokenNotExpired(token));
    }

    @Test
    void shouldActivateNewAccount() {
        //given
        final String email = "example@mail.com";
        final String token = "activation-token";

        UserRole userRole = new UserRole();
        userRole.setId(1L);
        userRole.setName("USER");

        User user = new User();
        user.setEmailverificationtoken(token);
        user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusMinutes(10));
        user.setEmailVerified(false);
        user.setEmail(email);
        user.setRoles(Set.of(userRole));

        Mockito.when(userRepositoryMock.findByEmailVerificationToken("activation-token")).thenReturn(Optional.of(user));

        //when
        userService.activateAccount("activation-token");

        //then
        assertNull(user.getEmailVerificationToken());
        assertNull(user.getEmailVerificationTokenExpiry());
        assertTrue(user.isEmailVerified());
    }
}