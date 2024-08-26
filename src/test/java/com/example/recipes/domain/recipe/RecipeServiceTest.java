package com.example.recipes.domain.recipe;

import com.example.recipes.domain.comment.CommentRepository;
import com.example.recipes.domain.difficultyLevel.DifficultyLevel;
import com.example.recipes.domain.difficultyLevel.DifficultyLevelRepository;
import com.example.recipes.domain.rating.RatingRepository;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeSaveDto;
import com.example.recipes.domain.type.Type;
import com.example.recipes.domain.type.TypeRepository;
import com.example.recipes.domain.type.dto.TypeDto;
import com.example.recipes.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {
    @Mock private RecipeRepository recipeRepositoryMock;
    @Mock private TypeRepository typeRepositoryMock;
    @Mock DifficultyLevelRepository difficultyLevelRepositoryMock;
    @Mock FileStorageService fileStorageServiceMock;
    @Mock RatingRepository ratingRepositoryMock;
    @Mock private CommentRepository commentRepositoryMock;
    @Mock private RecipeDateTimeProvider recipeDateTimeProviderMock;
    private RecipeService recipeService;

    @BeforeEach
    void init() {
        recipeService = new RecipeService(recipeRepositoryMock, typeRepositoryMock, difficultyLevelRepositoryMock, fileStorageServiceMock, ratingRepositoryMock, commentRepositoryMock, recipeDateTimeProviderMock);
    }

    @Test
    void findPaginated() {
    }

    @Test
    void findPaginatedRecipesList() {
    }

    @Test
    void shouldFindRecipeById() {
        //given
        Type type = new Type();
        type.setId(11L);
        type.setName("Zupa");

        DifficultyLevel difficultyLevel = new DifficultyLevel();
        difficultyLevel.setId(21L);
        difficultyLevel.setName("Trudne");

        Recipe recipe = new Recipe();
        recipe.setName("Pomidorowa");
        recipe.setType(type);
        recipe.setDescription("Soczyste kotlety z mielonej wołowiny a do tego pyszne i kolorowe warzywa i świeża bułka. Zapraszam po mój szybki i sprawdzony przepis na idealne, burgery wołowe o doskonałym smaku i kompozycji. Są rewelacyjne!");
        recipe.setPreparationTime(15);
        recipe.setCookingTime(20);
        recipe.setServing(5);
        recipe.setDifficultyLevel(difficultyLevel);
        recipe.setIngredients("kurczak\\ncurry\\ncebula\\\\nmleko kokosowe\\nprzyprawy");
        recipe.setDirections("Podsmaż cebulę i czosnek.\\nDodaj kurczaka i curry.\\nWlej mleko kokosowe.\\nGotuj na wolnym ogniu.");

        LocalDateTime now = LocalDateTime.now();
        recipe.setCreationDate(now);

        Mockito.when(recipeRepositoryMock.findById(11L)).thenReturn(Optional.of(recipe));

        //when
        RecipeFullInfoDto recipeFullInfoDto = recipeService.findRecipeById(11L).orElseThrow();

        //then
        assertEquals("Pomidorowa", recipeFullInfoDto.getName());
        assertEquals("Trudne", recipeFullInfoDto.getDifficultyLevel());
        assertEquals(15, recipeFullInfoDto.getPreparationTime());
        assertTrue(recipeFullInfoDto.getIngredients().contains("kurczak"));
        assertEquals(20, recipeFullInfoDto.getCookingTime());
        assertEquals(5, recipeFullInfoDto.getServing());
    }



    @Test
    void findRecipeToSave() {
    }

    @Test
    void findRecipesByType() {
    }

    @Test
    void shouldAddNewRecipe() {
        //given
        RecipeSaveDto recipeSaveDto = new RecipeSaveDto();
        recipeSaveDto.setName("Pomidorowa");
        recipeSaveDto.setType("Zupa");
        recipeSaveDto.setDescription("Soczyste kotlety z mielonej wołowiny a do tego pyszne i kolorowe warzywa i świeża bułka. Zapraszam po mój szybki i sprawdzony przepis na idealne, burgery wołowe o doskonałym smaku i kompozycji. Są rewelacyjne!");
        recipeSaveDto.setPreparationTime(15);
        recipeSaveDto.setCookingTime(20);
        recipeSaveDto.setServing(5);
        recipeSaveDto.setDifficultyLevel("Trudne");
        recipeSaveDto.setIngredients("kurczak\\ncurry\\ncebula\\\\nmleko kokosowe\\nprzyprawy");
        recipeSaveDto.setDirectionsSteps("Podsmaż cebulę i czosnek.\\nDodaj kurczaka i curry.\\nWlej mleko kokosowe.\\nGotuj na wolnym ogniu.");
        recipeSaveDto.setImage(null);

        LocalDateTime now = LocalDateTime.now();

        recipeSaveDto.setCreationDate(now);

        Type type = new Type();
        type.setId(11L);
        type.setName("Zupa");

        DifficultyLevel difficultyLevel = new DifficultyLevel();
        difficultyLevel.setId(21L);
        difficultyLevel.setName("Trudne");

        Mockito.when(recipeDateTimeProviderMock.getCurrentTime()).thenReturn(now);
        Mockito.when(typeRepositoryMock.findByNameIgnoreCase("Zupa")).thenReturn(Optional.of(type));
        Mockito.when(difficultyLevelRepositoryMock.findByName("Trudne")).thenReturn(Optional.of(difficultyLevel));

        //when
        recipeService.addRecipe(recipeSaveDto);

        //then
        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
        Mockito.verify(recipeRepositoryMock).save(recipeCaptor.capture());

        Recipe recipeCaptorValue = recipeCaptor.getValue();
        assertEquals("Pomidorowa", recipeCaptorValue.getName());
        assertEquals("Trudne", recipeCaptorValue.getDifficultyLevel().getName());
        assertEquals(15, recipeCaptorValue.getPreparationTime());
        assertTrue(recipeCaptorValue.getIngredients().contains("cebula"));
        assertEquals(20, recipeCaptorValue.getCookingTime());
        assertEquals(5, recipeCaptorValue.getServing());
        assertNull(recipeCaptorValue.getImage());
        assertEquals(now, recipeCaptorValue.getCreationDate());
    }

   @Test
   void shouldThrowExceptionWhenAddRecipeWithNotExistingType(){
        //give
       RecipeSaveDto recipeSaveDto = new RecipeSaveDto();
       recipeSaveDto.setName("Pomidorowa");
       recipeSaveDto.setType("NieistniejącyTyp");

       Mockito.when(typeRepositoryMock.findByNameIgnoreCase("NieistniejącyTyp")).thenReturn(Optional.empty());

       //when
       //then
       assertThrows(NoSuchElementException.class, () -> recipeService.addRecipe(recipeSaveDto));
   }

    @Test
    void findRecipesByText() {
    }

    @Test
    void findFavouriteRecipesForUser() {
    }

    @Test
    void countFavouriteUserRecipes() {
    }

    @Test
    void findRatedRecipesByUser() {
    }

    @Test
    void countRatedRecipeByUser() {
    }

    @Test
    void updateRecipe() {
    }

    @Test
    void deleteRecipe() {
    }
}