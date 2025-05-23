package com.example.recipes.domain.recipe;

public interface PdfGenerator {
    byte[] generatePdfRecipe(long recipeId);
}
