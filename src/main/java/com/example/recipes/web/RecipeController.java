package com.example.recipes.web;

import com.example.recipes.domain.comment.CommentService;
import com.example.recipes.domain.comment.dto.CommentDto;
import com.example.recipes.domain.rating.RatingService;
import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class RecipeController {
    public final static int PAGE_SIZE = 6;
    private final RecipeService recipeService;
    private final RatingService ratingService;
    private final UserService  userService;
    private final CommentService commentService;

    public RecipeController(RecipeService recipeService, RatingService ratingService, UserService userService, CommentService commentService) {
        this.recipeService = recipeService;
        this.ratingService = ratingService;
        this.userService = userService;
        this.commentService = commentService;
    }

    private static final Map<String, String> SORT_FIELD_MAP = new HashMap<>();
    static {
        SORT_FIELD_MAP.put("dataPublikacji", "creationDate");
        SORT_FIELD_MAP.put("nazwa", "name");
    }

    @GetMapping("/przepis/{id}")
    public String getRecipe(@PathVariable long id,
                            Model model,
                            Authentication authentication){
        RecipeFullInfoDto recipeFullInfoDto = recipeService.findRecipeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("recipe", recipeFullInfoDto);
        if (authentication != null){
            String currentUserName = authentication.getName();
            Integer rating = ratingService.getRatingForRecipe(currentUserName, id).orElse(0);
            model.addAttribute("userRating", rating);
            CommentDto comment = new CommentDto();
            model.addAttribute("comment", comment);
        }
        int favourites = userService.favoritesCount(id);
        model.addAttribute("favourites", favourites);
        List<CommentDto> comments = commentService.getCommentsForRecipe(id);
        model.addAttribute("comments", comments);
        return "recipe";
    }

    @GetMapping("/strona/{pageNo}")
    public String getAllRecipesPageable(@PathVariable Optional<Integer> pageNo,
                                        @RequestParam(value ="poleSortowania", required = false) String poleSortowania,
                                        Model model){
        int pageNumber = pageNo.orElse(1);
        String sortField = SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
        Page<RecipeMainInfoDto> recipePage = recipeService.findPaginated(pageNumber, PAGE_SIZE, sortField);
        List<RecipeMainInfoDto> recipes = recipePage.getContent();
        model.addAttribute("recipes", recipes);
        int totalPages = recipePage.getTotalPages();
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("heading", "Wszytskie przepisy");
        model.addAttribute("sortField", poleSortowania);
        model.addAttribute("baseUrl", "/strona");
        return "recipe-listing";
    }


    @GetMapping("/szukaj/strona/{pageNo}")
    public String getRecipesBySearchText(@RequestParam String searchText,
                                         @PathVariable Optional<Integer> pageNo,
                                         @RequestParam(value = "poleSortowania", required = false) String poleSortowania,
                                         Model model) {
        int pageNumber = pageNo.orElse(1);
        String sortField = SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
        Page<RecipeMainInfoDto> recipesPageByText = recipeService.findRecipesByText(searchText, pageNumber, PAGE_SIZE, sortField);
        List<RecipeMainInfoDto> recipes = recipesPageByText.getContent();
        int totalPages = recipesPageByText.getTotalPages();
        model.addAttribute("recipes", recipes);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("sortField", poleSortowania);
        model.addAttribute("baseUrl", "/szukaj/strona");
        model.addAttribute("heading", "Wyszukiwane przepisy");
        model.addAttribute("searchText", searchText);
        return "recipe-listing";
    }


    @GetMapping("/ulubione/strona/{pageNo}")
    public String getFavouriteRecipesForUser(@PathVariable Optional<Integer> pageNo,
                                             @RequestParam(value = "poleSortowania", required = false) String poleSortowania,
                                             Authentication authentication,
                                             Model model){
        String email = authentication.getName();
        int pageNumber = pageNo.orElse(1);
        String sortField = SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
        Page<RecipeMainInfoDto> favouriteRecipesPagesForUser = recipeService.findFavouriteRecipesForUser(email, pageNumber, PAGE_SIZE, sortField);
        List<RecipeMainInfoDto> recipes = favouriteRecipesPagesForUser.getContent();
        int totalPages = favouriteRecipesPagesForUser.getTotalPages();
        model.addAttribute("recipes", recipes);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("sortField", poleSortowania);
        model.addAttribute("baseUrl", "/ulubione/strona");
        model.addAttribute("heading", "Twoje ulubione przepisy");
        return "recipe-listing";
    }

    @GetMapping("/ocenione/strona/{pageNo}")
    public String getRatedRecipesForUser(@PathVariable Optional<Integer> pageNo,
                                         @RequestParam(value = "poleSortowania", required = false) String poleSortowania,
                                         Authentication authentication,
                                         Model model){
        String email = authentication.getName();
        int pageNumber = pageNo.orElse(1);
        String sortField = SORT_FIELD_MAP.getOrDefault(poleSortowania, "creationDate");
        Page<RecipeMainInfoDto> ratedRecipesPagesForUser = recipeService.findRatedRecipesForUser(email, pageNumber, PAGE_SIZE, sortField);
        List<RecipeMainInfoDto> recipes = ratedRecipesPagesForUser.getContent();
        int totalPages = ratedRecipesPagesForUser.getTotalPages();
        model.addAttribute("recipes", recipes);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("sortField", poleSortowania);
        model.addAttribute("baseUrl", "/ocenione/strona");
        model.addAttribute("heading", "Twoje oceninone przepisy");
        return "recipe-listing";
    }
}
