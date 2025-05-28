package com.example.recipes.domain.recipe;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfRecipeGeneratorService implements PdfGenerator {

    private final RecipeRepository recipeRepository;

    public PdfRecipeGeneratorService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    private static final Font LOGO_FONT = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(0, 100,0));
    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font SECTION_FONT = new Font(Font.HELVETICA, 16, Font.BOLD);
    private static final Font REGULAR_FONT = new Font(Font.HELVETICA, 14);

    @Override
    public byte[] generatePdfRecipe(long recipeId){

        try(ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream()) {
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            addPhoto(recipe, document);
            addLogoTitle(document, recipe);
            addRecipeDescription(recipe, document);
            addIngredients(document, recipe);
            addDescriptionSteps(document, recipe);

            document.close();
            return byteArrayOutputStream.toByteArray();
        } catch (DocumentException | IOException e){
           throw new RuntimeException("Błąd podczas generowania PDF-a", e);
        }
    }

    private static void addDescriptionSteps(Document document, Recipe recipe) {
        Paragraph descriptionStepsSectionParagraph = new Paragraph("Przygotowanie", SECTION_FONT);
        descriptionStepsSectionParagraph.setSpacingBefore(20);
        document.add(descriptionStepsSectionParagraph);
        List descriptionStepsList= new List(List.UNORDERED);
        descriptionStepsList.setListSymbol(new Chunk("✔ ", REGULAR_FONT));
        String[] descriptionSteps = recipe.getDirections().split("\\\\n");
        for (String descriptionStep : descriptionSteps) {
            descriptionStepsList.add(new ListItem(descriptionStep, REGULAR_FONT));
        }
        document.add(descriptionStepsList);
    }

    private static void addIngredients(Document document, Recipe recipe) {
        Paragraph ingredientsTitleSectionParagraph  = new Paragraph("Składniki", SECTION_FONT);
        ingredientsTitleSectionParagraph .setSpacingBefore(20);
        document.add(ingredientsTitleSectionParagraph );

        List ingredientsList = new List(List.UNORDERED);
        ingredientsList.setListSymbol(new Chunk("✔ ", REGULAR_FONT));
        String[] ingredients = recipe.getIngredients().split("\\\\n");
        for (String ingredient : ingredients) {
            ingredientsList.add(new ListItem(ingredient, REGULAR_FONT));
        }
        document.add(ingredientsList);
    }

    private static void addRecipeDescription(Recipe recipe, Document document) {
        Paragraph recipeDescription = new Paragraph(recipe.getDescription(), REGULAR_FONT);
        recipeDescription.setSpacingAfter(15);
        document.add(recipeDescription);
    }

    private static void addLogoTitle(Document document, Recipe recipe) {
        Paragraph logoTitle = new Paragraph("Kuchnia Bartosza", LOGO_FONT);
        logoTitle.setSpacingBefore(30);
        logoTitle.setSpacingAfter(50);
        document.add(logoTitle);

        Paragraph recipeTitleParagraph = new Paragraph(recipe.getName(), TITLE_FONT);
        recipeTitleParagraph.setSpacingAfter(15);
        document.add(recipeTitleParagraph);
    }

    private static void addPhoto(Recipe recipe, Document document) throws IOException {
        Image photo = Image.getInstanceFromClasspath(recipe.getImage()!= null ? "static/img/%s".formatted(recipe.getImage()) : "static/img/photo-dish.jpg");
        photo.scaleAbsolute(200, 140);
        photo.setAbsolutePosition(350, 660);
        document.add(photo);
    }
}
