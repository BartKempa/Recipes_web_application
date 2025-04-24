package com.example.recipes.domain.user;

import com.example.recipes.domain.comment.CommentRepository;
import com.example.recipes.domain.rating.RatingRepository;
import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.user.dto.UserCredentialsDto;
import com.example.recipes.domain.user.dto.UserRegistrationDto;
import com.example.recipes.domain.user.dto.UserRetrievePasswordDto;
import com.example.recipes.domain.user.dto.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private static final String DEFAULT_USER_ROLE = "USER";
    private static final String RESET_PASSWORD_SUBJECT = "Reset hasła";
    private static final String ACTIVATION_MAIL_SUBJECT = "Aktywacja nowego konta";
    private static final String MAIL_ADDRESS = "kuchniabartosza@gmail.com";
    private static final String RESET_PASSWORD_MAIL_TEXT = "Możesz zresetować swoje hasło klikając poniższy link: \n ";
    private static final String ACTIVATION_ACCOUNT_MAIL_TEXT = "W celu zakończenia rejestracji kliknij poniższy link: \n ";
    private static final String RESET_LINK = "http://localhost:8080/reset-hasla?token=%s";
    private static final String ACTIVATION_LINK = "http://localhost:8080/aktywacja-konta?token=%s";

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;
    private final JavaMailSender javaMailSender;


    UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder, RecipeRepository recipeRepository, CommentRepository commentRepository, RatingRepository ratingRepository, JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.recipeRepository = recipeRepository;
        this.commentRepository = commentRepository;
        this.ratingRepository = ratingRepository;
        this.javaMailSender = javaMailSender;
    }

    public Optional<UserCredentialsDto> findCredentialsByEmail(String email) {
        if (email == null || email.isBlank()){
            throw new IllegalArgumentException("Email cannot be null");
        }
        return userRepository.findByEmail(email)
                .map(UserCredentialsDtoMapper::map);
    }

    @Transactional
    public void registerUserWithDefaultRole(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        user.setEmail(userRegistrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setNickName(userRegistrationDto.getNickName());
        user.setAge(userRegistrationDto.getAge());
        user.setEmailVerified(false);
        String activationToken = generateToken();
        user.setEmailverificationtoken(activationToken);
        user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(12));
        sendActivationEmail(userRegistrationDto.getEmail(), activationToken);
        UserRole userRole = userRoleRepository.findByName(DEFAULT_USER_ROLE).orElseThrow();
        user.getRoles().add(userRole);
        userRepository.save(user);
    }

    private void sendActivationEmail(String email, String token) {
        String activationLink = ACTIVATION_LINK.formatted(token);
        String emailContent = ACTIVATION_ACCOUNT_MAIL_TEXT + activationLink;
        sendEmail(email, ACTIVATION_MAIL_SUBJECT, emailContent);
    }


    @Transactional
    public void addOrUpdateFavoriteRecipe(String userEmail, long recipeId) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
        if (user.getFavoriteRecipes().contains(recipe)) {
            user.getFavoriteRecipes().remove(recipe);
        } else {
            user.getFavoriteRecipes().add(recipe);
        }
        userRepository.save(user);
    }

    public int favoritesCount(long recipeId) {
        return userRepository.countUsersByFavouriteRecipe(recipeId);
    }

    public Optional<UserRegistrationDto> findUserById(long userId) {
        return userRepository.findById(userId)
                .map(UserRegistrationDtoMapper::map);
    }

    public Optional<UserRegistrationDto> findUserByName(String userName) {
        return userRepository.findByEmail(userName)
                .map(UserRegistrationDtoMapper::map);
    }

    public Optional<UserUpdateDto> findUserToUpdateById(long userId) {
        return userRepository.findById(userId)
                .map(UserUpdateDtoMapper::map);
    }

    public Optional<UserUpdateDto> findUserToUpdateByEmail(String userName){
        return userRepository.findByEmail(userName)
                .map(UserUpdateDtoMapper::map);
    }

    @Transactional
    public void updateUserData(UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(userUpdateDto.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setFirstName(userUpdateDto.getFirstName());
        user.setLastName(userUpdateDto.getLastName());
        user.setNickName(userUpdateDto.getNickName());
        user.setAge(userUpdateDto.getAge());
        userRepository.save(user);
    }

    @Transactional
    public void updateUserPassword(UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(userUpdateDto.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        commentRepository.deleteAll(user.getComments());
        user.getFavoriteRecipes().clear();
        user.getRoles().clear();
        ratingRepository.deleteAll(user.getRatings());
        userRepository.delete(user);
    }

    public Page<UserRegistrationDto> findAllUsers(int pageNumber, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize, sort);
        return userRepository.findAll(pageable)
                .map(UserRegistrationDtoMapper::map);
    }

    public boolean checkEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void sendResetLink(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String token = generateToken();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(5));
        String resetLink = RESET_LINK.formatted(token);
        String emailContent = RESET_PASSWORD_MAIL_TEXT + resetLink;

        sendEmail(email, RESET_PASSWORD_SUBJECT, emailContent);
    }

    private void sendEmail(String to, String subject, String text){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        mailMessage.setFrom(MAIL_ADDRESS);

        javaMailSender.send(mailMessage);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public boolean checkResetTokenExists(String token) {
        return userRepository.findByPasswordResetToken(token).isPresent();
    }

    public boolean checkResetTokenNotExpired(String token) {
        return userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getPasswordResetTokenExpiry().isAfter(LocalDateTime.now());
    }

    public Optional<UserRetrievePasswordDto> findUserToRetrievePasswordByToken(String token) {
        return userRepository.findByPasswordResetToken(token)
                .map(UserRetrievePasswordDtoMapper::map);
    }

    @Transactional
    public void retrieveUserPassword(UserRetrievePasswordDto userRetrievePasswordDto) {
        User user = userRepository.findById(userRetrievePasswordDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setPassword(passwordEncoder.encode(userRetrievePasswordDto.getPassword()));
        user.setPasswordResetTokenExpiry(null);
        user.setPasswordResetToken(null);
    }

    public boolean checkActivationTokenExists(String token) {
        return userRepository.findByEmailVerificationToken(token).isPresent();
    }

    public boolean checkActivationTokenNotExpired(String token) {
        return userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getEmailVerificationTokenExpiry().isAfter(LocalDateTime.now());
    }

    @Transactional
    public void activateAccount(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setEmailVerified(true);
        user.setEmailVerificationTokenExpiry(null);
        user.setEmailverificationtoken(null);
    }
}


