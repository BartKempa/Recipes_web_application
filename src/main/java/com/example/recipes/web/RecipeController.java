package com.example.recipes.web;

import com.example.recipes.domain.comment.CommentService;
import com.example.recipes.domain.comment.dto.CommentDto;
import com.example.recipes.domain.rating.RatingService;
import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Controller
public class RecipeController {
    private final static int PAGE_SIZE = 6;
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
                                        @RequestParam("sortField") String sortField,
                                        Model model){
        int pageNumber = pageNo.orElse(1);
        Page<RecipeMainInfoDto> recipePage = recipeService.findPaginated(pageNumber, PAGE_SIZE, sortField);
        List<RecipeMainInfoDto> recipes = recipePage.getContent();
        model.addAttribute("recipes", recipes);
        int totalPages = recipePage.getTotalPages();
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("heading", "Wszytskie przepisy");
        model.addAttribute("sortField", sortField);
        model.addAttribute("baseUrl", "/strona");
        return "recipe-listing";
    }

    @PostMapping("/search")
    public String getRecipesBySearchText(Model model, @RequestParam String searchText){
        List<RecipeMainInfoDto> recipes = recipeService.findRecipesByText(searchText);
        System.out.println("liczba wyszukanych przepisów " + recipes.size());

        model.addAttribute("recipes", recipes);
        model.addAttribute("heading", "Wyszkiwane przepisy");
        return "recipe-listing";
    }



}
