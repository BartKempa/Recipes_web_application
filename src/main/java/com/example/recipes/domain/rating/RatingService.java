package com.example.recipes.domain.rating;

import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RatingService(RatingRepository ratingRepository, RecipeRepository recipeRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addOrUpdateRating(String userEmail, long recipeId, int rating){
        Rating ratingToSaveOrUpdate = ratingRepository.findByUser_EmailAndRecipe_Id(userEmail, recipeId).orElseGet(Rating::new);
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
        ratingToSaveOrUpdate.setUser(user);
        ratingToSaveOrUpdate.setRecipe(recipe);
        ratingToSaveOrUpdate.setRating(rating);
        ratingRepository.save(ratingToSaveOrUpdate);
    }

    public Optional<Integer> getRatingForRecipe(String userEmail, long recipeId){
        return ratingRepository.findByUser_EmailAndRecipe_Id(userEmail, recipeId)
                .map(Rating::getRating);
    }

    public Map<Long, Integer> getUserRatings(String email){
        List<Rating> userRatings = ratingRepository.findAllByUser_Email(email);
        return userRatings.stream().collect(Collectors.toMap(rating -> rating.getRecipe().getId(), Rating::getRating));
    }
}
