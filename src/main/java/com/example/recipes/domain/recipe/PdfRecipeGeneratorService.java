package com.example.recipes.domain.recipe;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfRecipeGeneratorService implements PdfGenerator {

    private final RecipeRepository recipeRepository;

    public PdfRecipeGeneratorService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font SECTION_FONT = new Font(Font.HELVETICA, 14, Font.BOLD);
    private static final Font REGULAR_FONT = new Font(Font.HELVETICA, 12);

    @Override
    public byte[] generatePdfRecipe(long recipeId){
        try(ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream()) {
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            Document document = new Document(PageSize.A4);

            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            Paragraph recipeTitleParagraph = new Paragraph(recipe.getName(), TITLE_FONT);
            document.add(recipeTitleParagraph);

            Paragraph ingredientsTitileSectionParagraph = new Paragraph("Składniki", SECTION_FONT);

            List ingredientsList= new List(List.UNORDERED);
            String[] ingredients = recipe.getIngredients().split("\\\\n");
            for (String ingredient : ingredients) {
                ingredientsList.add(new ListItem(ingredient, REGULAR_FONT));
            }

            document.add(ingredientsTitileSectionParagraph);
            document.add(ingredientsList);
            document.close();
            return byteArrayOutputStream.toByteArray();
        } catch (DocumentException | IOException e){
           throw new RuntimeException("Błąd podczas generowania PDF-a", e);
        }
    }


}
