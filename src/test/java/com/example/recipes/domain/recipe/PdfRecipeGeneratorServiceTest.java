package com.example.recipes.domain.recipe;

import com.example.recipes.domain.difficultyLevel.DifficultyLevel;
import com.example.recipes.domain.type.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PdfRecipeGeneratorServiceTest {

    @Mock
    private RecipeRepository recipeRepositoryMock;

    private PdfRecipeGeneratorService pdfRecipeGeneratorService;

    @BeforeEach
    void init() {
        pdfRecipeGeneratorService = new PdfRecipeGeneratorService(recipeRepositoryMock);
    }

    @Test
    void shouldGeneratePdfRecipe() {
        //given
        Type type = new Type();
        type.setId(11L);
        type.setName("Zupa");

        DifficultyLevel difficultyLevel = new DifficultyLevel();
        difficultyLevel.setId(21L);
        difficultyLevel.setName("Trudne");

        Recipe recipe = new Recipe();
        recipe.setName("Agrestowa");
        recipe.setDescription("Soczyste kotlety z mielonej wołowiny a do tego pyszne i kolorowe warzywa i świeża bułka.");
        recipe.setIngredients("mąka\\nmleko\\njajka\\nser\\ncukier\\nsól");
        recipe.setDirections("Zmiksuj wszystkie składniki na ciasto.\\nUsmaż naleśniki.\\nNadziewaj serem.\\nZapiekaj w piekarniku.");
        recipe.setType(type);
        recipe.setCreationDate(LocalDateTime.now());

        Mockito.when(recipeRepositoryMock.findById(11L)).thenReturn(Optional.of(recipe));

        //when
        byte[] generatePdfRecipe = pdfRecipeGeneratorService.generatePdfRecipe(11L);

        //then
        assertNotNull(generatePdfRecipe);
        assertTrue(generatePdfRecipe.length > 0);
    }

    @Test
    void shouldThrowExceptionWhenRecipeNotFound() {
        Mockito.when(recipeRepositoryMock.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> pdfRecipeGeneratorService.generatePdfRecipe(99L));
    }
}