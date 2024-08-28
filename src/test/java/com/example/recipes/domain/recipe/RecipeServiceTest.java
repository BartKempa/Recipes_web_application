package com.example.recipes.domain.recipe;

import com.example.recipes.domain.comment.CommentRepository;
import com.example.recipes.domain.difficultyLevel.DifficultyLevel;
import com.example.recipes.domain.difficultyLevel.DifficultyLevelRepository;
import com.example.recipes.domain.rating.Rating;
import com.example.recipes.domain.rating.RatingRepository;
import com.example.recipes.domain.recipe.dto.RecipeFullInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.recipe.dto.RecipeSaveDto;
import com.example.recipes.domain.type.Type;
import com.example.recipes.domain.type.TypeRepository;
import com.example.recipes.domain.type.dto.TypeDto;
import com.example.recipes.domain.user.User;
import com.example.recipes.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
    void shouldFindTwoPaginatedRecipesSortedDescByName() {
        //given
        Type type = new Type();
        type.setId(11L);
        type.setName("Zupa");

        DifficultyLevel difficultyLevel = new DifficultyLevel();
        difficultyLevel.setId(21L);
        difficultyLevel.setName("Trudne");

        Recipe recipe1 = new Recipe();
        recipe1.setName("Agrestowa");
        recipe1.setType(type);
        recipe1.setCreationDate(LocalDateTime.now());

        Recipe recipe2 = new Recipe();
        recipe2.setName("Burczkowa");
        recipe2.setType(type);
        recipe2.setCreationDate(LocalDateTime.now().minusDays(1));

        List<Recipe> recipeList = Arrays.asList(recipe2, recipe1);
        Page<Recipe> recipePage = new PageImpl<>(recipeList);

        Mockito.when(recipeRepositoryMock.findAll(Mockito.any(Pageable.class))).thenReturn(recipePage);

        int pageNumber = 1;
        int pageSize = 4;
        String sortField = "name";

        //when
        Page<RecipeMainInfoDto> recipeMainInfoDtoPage = recipeService.findPaginated(pageNumber, pageSize, sortField);

        //then
        assertEquals(2, recipeMainInfoDtoPage.getTotalElements());
        assertEquals("Burczkowa", recipeMainInfoDtoPage.getContent().get(0).getName());
        assertEquals("Agrestowa", recipeMainInfoDtoPage.getContent().get(1).getName());
    }

    @Test
    void shouldFindPaginatedRecipesListSortingByCreationDateAsc() {
        //given
        Type type = new Type();
        type.setId(11L);
        type.setName("Zupa");

        DifficultyLevel difficultyLevel = new DifficultyLevel();
        difficultyLevel.setId(21L);
        difficultyLevel.setName("Trudne");

        Recipe recipe1 = new Recipe();
        recipe1.setName("Agrestowa");
        recipe1.setType(type);
        recipe1.setCreationDate(LocalDateTime.now());

        Recipe recipe2 = new Recipe();
        recipe2.setName("Burczkowa");
        recipe2.setType(type);
        recipe2.setCreationDate(LocalDateTime.now().minusDays(1));

        List<Recipe> recipeList = Arrays.asList(recipe1, recipe2);

        Mockito.when(recipeRepositoryMock.findAll(Mockito.any(Pageable.class))).thenAnswer(invocationOnMock -> {
            Pageable pageable = invocationOnMock.getArgument(0);
            List<Recipe> sortedList = recipeList.stream().sorted((r1, r2) -> {
                if (pageable.getSort().getOrderFor("creationDate") != null && pageable.getSort().getOrderFor("creationDate").isAscending()) {
                    return r1.getCreationDate().compareTo(r2.getCreationDate());
                } else {
                    return r2.getCreationDate().compareTo(r1.getCreationDate());
                }
            }).toList();
         return new PageImpl<>(sortedList, pageable, sortedList.size());
        });

        int pageNumber = 1;
        int pageSize = 3;
        String sortField = "creationDate";
        String sortDirection = "asc";

        //when
        Page<RecipeMainInfoDto> paginatedRecipesList = recipeService.findPaginatedRecipesList(pageNumber, pageSize, sortField, sortDirection);

        //then
        assertEquals(2, paginatedRecipesList.getTotalElements());
        assertEquals("Burczkowa", paginatedRecipesList.getContent().get(0).getName());
        assertEquals("Agrestowa", paginatedRecipesList.getContent().get(1).getName());
    }

    @Test
    void shouldFindPaginatedRecipesListSortingByNameDesc() {
        //given
        Type type = new Type();
        type.setId(11L);
        type.setName("Zupa");

        DifficultyLevel difficultyLevel = new DifficultyLevel();
        difficultyLevel.setId(21L);
        difficultyLevel.setName("Trudne");

        Recipe recipe1 = new Recipe();
        recipe1.setName("Agrestowa");
        recipe1.setType(type);
        recipe1.setCreationDate(LocalDateTime.now());

        Recipe recipe2 = new Recipe();
        recipe2.setName("Burczkowa");
        recipe2.setType(type);
        recipe2.setCreationDate(LocalDateTime.now().minusDays(1));

        List<Recipe> recipeList = Arrays.asList(recipe1, recipe2);

        Mockito.when(recipeRepositoryMock.findAll(Mockito.any(Pageable.class))).thenAnswer(invocationOnMock -> {
            Pageable pageable = invocationOnMock.getArgument(0);
            List<Recipe> sortedList = recipeList.stream().sorted((r1, r2) -> {
                if (pageable.getSort().getOrderFor("name") != null && pageable.getSort().getOrderFor("name").isAscending()) {
                    return r1.getName().compareTo(r2.getName());
                } else {
                    return r2.getName().compareTo(r1.getName());
                }
            }).toList();
            return new PageImpl<>(sortedList, pageable, sortedList.size());
        });

        int pageNumber = 1;
        int pageSize = 3;
        String sortField = "name";
        String sortDirection = "desc";

        //when
        Page<RecipeMainInfoDto> paginatedRecipesList = recipeService.findPaginatedRecipesList(pageNumber, pageSize, sortField, sortDirection);

        //then
        assertEquals(2, paginatedRecipesList.getTotalElements());
        assertEquals("Burczkowa", paginatedRecipesList.getContent().get(0).getName());
        assertEquals("Agrestowa", paginatedRecipesList.getContent().get(1).getName());
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
    void shouldThrowExceptionWhenRecipeNotExists(){
        //given
        Mockito.when(recipeRepositoryMock.findById(2L)).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(NoSuchElementException.class, () -> recipeService.findRecipeById(2L).orElseThrow());
    }

    @Test
    void shouldFindRecipeToSave() {
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
        RecipeSaveDto recipeSaveDto = recipeService.findRecipeToSave(11L).orElseThrow();

        //then
        assertEquals("Pomidorowa", recipeSaveDto.getName());
        assertEquals("Trudne", recipeSaveDto.getDifficultyLevel());
        assertEquals(15, recipeSaveDto.getPreparationTime());
        assertTrue(recipeSaveDto.getIngredients().contains("kurczak"));
        assertEquals(20, recipeSaveDto.getCookingTime());
        assertEquals(5, recipeSaveDto.getServing());
    }

    @Test
    void shouldThrowExceptionWhenTryFindNotExistsRecipeToSave(){
        //given
        Mockito.when(recipeRepositoryMock.findById(123L)).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(NoSuchElementException.class, () -> recipeService.findRecipeToSave(123L).orElseThrow());
    }

    @Test
    void shouldFindTwoRecipesByType() {
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

        LocalDateTime now = LocalDateTime.now();
        recipe.setCreationDate(now);

        Recipe recipe2 = new Recipe();
        recipe2.setName("Buraczkowa");
        recipe2.setType(type);

        LocalDateTime now2 = LocalDateTime.now();
        recipe.setCreationDate(now2);

        List<Recipe> recipeList = Arrays.asList(recipe, recipe2);
        Page<Recipe> recipePage = new PageImpl<>(recipeList);

        Mockito.when(recipeRepositoryMock.findAllByType_NameIgnoreCase(Mockito.eq("Zupa"), Mockito.any(Pageable.class))).thenReturn(recipePage);

        int pageNumber = 1;
        int pageSize = 4;

        //when
        Page<RecipeMainInfoDto> pageRecipesByType = recipeService.findRecipesByType("Zupa", pageNumber, pageSize);

        //then
        assertThat(pageRecipesByType.getContent().get(0).getType(), is("Zupa"));
        assertThat(pageRecipesByType.getContent().get(0).getName(), is("Pomidorowa"));
        assertThat(pageRecipesByType.getContent().get(1).getName(), is("Buraczkowa"));
        assertThat(pageRecipesByType.getContent().get(0).getCreationDate(), is(now));
        assertThat(pageRecipesByType.getContent().get(1).getCreationDate(), is(now2));
        assertThat(pageRecipesByType.getTotalElements(), is(2));
    }

    @Test
    void shouldReturnEmptyPageWhenNoRecipesFound() {
        // given
        Mockito.when(recipeRepositoryMock.findAllByType_NameIgnoreCase(Mockito.eq("NieistniejącyTyp"), Mockito.any(Pageable.class)))
                .thenReturn(Page.empty());

        // when
        Page<RecipeMainInfoDto> pageRecipesByType = recipeService.findRecipesByType("NieistniejącyTyp", 1, 4);

        // then
        assertTrue(pageRecipesByType.isEmpty());
    }

    @Test
    void shouldFindRecipesWhenIgnoreCase() {
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

        LocalDateTime now = LocalDateTime.now();
        recipe.setCreationDate(now);

        Recipe recipe2 = new Recipe();
        recipe2.setName("Buraczkowa");
        recipe2.setType(type);

        LocalDateTime now2 = LocalDateTime.now();
        recipe.setCreationDate(now2);

        List<Recipe> recipeList = Arrays.asList(recipe, recipe2);
        Page<Recipe> recipePage = new PageImpl<>(recipeList);

        Mockito.when(recipeRepositoryMock.findAllByType_NameIgnoreCase(Mockito.eq("zupa"), Mockito.any(Pageable.class)))
                .thenReturn(recipePage);

        // when
        Page<RecipeMainInfoDto> pageRecipesByType = recipeService.findRecipesByType("zupa", 1, 4);

        // then
        assertFalse(pageRecipesByType.isEmpty());
        assertThat(pageRecipesByType.getContent().get(0).getType(), is("Zupa"));
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
    void shouldFindTwoFavouriteRecipesForUserSortingByName() {
        //given
        Type type = new Type();
        type.setId(11L);
        type.setName("Zupa");

        DifficultyLevel difficultyLevel = new DifficultyLevel();
        difficultyLevel.setId(21L);
        difficultyLevel.setName("Trudne");

        Recipe recipe1 = new Recipe();
        recipe1.setName("Agrestowa");
        recipe1.setType(type);
        recipe1.setCreationDate(LocalDateTime.now().minusDays(1));

        Recipe recipe2 = new Recipe();
        recipe2.setName("Burczkowa");
        recipe2.setType(type);
        recipe2.setCreationDate(LocalDateTime.now());

        Set<Recipe> favoriteRecipes = new HashSet<>();
        favoriteRecipes.add(recipe1);
        favoriteRecipes.add(recipe2);

        User user = new User();
        user.setEmail("john@mail.com");
        user.setFavoriteRecipes(favoriteRecipes);

        List<Recipe> favouriteRecipeList = Arrays.asList(recipe2, recipe1);
        Page<Recipe> recipePage = new PageImpl<>(favouriteRecipeList);

        Mockito.when(recipeRepositoryMock.findAllFavouritesRecipesForUser(Mockito.eq("john@mail.com"), Mockito.any(Pageable.class))).thenReturn(recipePage);

        int pageNumber = 1;
        int pageSize = 4;
        String sortField = "name";

        //when
        Page<RecipeMainInfoDto> favouriteRecipesForUser = recipeService.findFavouriteRecipesForUser("john@mail.com", pageNumber, pageSize, sortField);

        //then
        assertNotNull(favouriteRecipesForUser);
        assertThat(favouriteRecipesForUser.getSize(), is(2));
        assertThat(favouriteRecipesForUser.getTotalElements(), is(2L));
        assertThat(favouriteRecipesForUser.getContent().get(0).getName(), is("Burczkowa"));
        assertThat(favouriteRecipesForUser.getContent().get(1).getName(), is("Agrestowa"));
    }

    @Test
    void shouldFindTwoRecipesByText() {
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

        Recipe recipe2 = new Recipe();
        recipe2.setName("Burczkowa");
        recipe2.setType(type);
        recipe2.setDescription("Soczyste kotlety z mielonej wołowiny a do tego pyszne i kolorowe warzywa i świeża bułka. Zapraszam po mój szybki i sprawdzony przepis na idealne, burgery wołowe o doskonałym smaku i kompozycji. Są rewelacyjne!");
        recipe2.setPreparationTime(10);
        recipe2.setCookingTime(10);
        recipe2.setServing(10);
        recipe2.setDifficultyLevel(difficultyLevel);
        recipe2.setIngredients("curry\\ncebula\\\\nmleko kokosowe\\nprzyprawy");
        recipe2.setDirections("Podsmaż cebulę i czosnek.\\nDodaj curry.\\nWlej mleko kokosowe.\\nGotuj na wolnym ogniu.");

        LocalDateTime now2 = LocalDateTime.now();
        recipe2.setCreationDate(now2);

        List<Recipe> recipeList = Arrays.asList(recipe, recipe2);
        Page<Recipe> pageRecipes = new PageImpl<>(recipeList);


        Mockito.when(recipeRepositoryMock.findRecipesBySearchText(Mockito.eq("kotlety"), Mockito.any(Pageable.class))).thenReturn(pageRecipes);


        int pageNumber = 1;
        int pageSize = 4;
        String sortField = "name";

        //when
        Page<RecipeMainInfoDto> mainInfoDtoPage = recipeService.findRecipesByText("kotlety", pageNumber, pageSize, sortField);

        //then
        assertThat(mainInfoDtoPage.getContent().size(), is(2));
    }

    @Test
    void shouldFindTwoRecipesByTextAndSortResultByCreationDate() {
        //given
        Type type = new Type();
        type.setId(11L);
        type.setName("Zupa");

        DifficultyLevel difficultyLevel = new DifficultyLevel();
        difficultyLevel.setId(21L);
        difficultyLevel.setName("Trudne");

        Recipe recipe1 = new Recipe();
        recipe1.setName("Pomidorowa");
        recipe1.setType(type);
        recipe1.setCreationDate(LocalDateTime.now().minusDays(1));
        recipe1.setIngredients("kurczak\\ncurry\\ncebula\\\\nmleko kokosowe\\nprzyprawy");
        recipe1.setDirections("Podsmaż cebulę i czosnek.\\nDodaj kurczaka i curry.\\nWlej mleko kokosowe.\\nGotuj na wolnym ogniu.");

        Recipe recipe2 = new Recipe();
        recipe2.setName("Burczkowa");
        recipe2.setType(type);
        recipe2.setCreationDate(LocalDateTime.now());
        recipe2.setIngredients("kurczak\\ncurry\\ncebula\\\\nmleko kokosowe\\nprzyprawy");
        recipe2.setDirections("Podsmaż cebulę i czosnek.\\nDodaj kurczaka i curry.\\nWlej mleko kokosowe.\\nGotuj na wolnym ogniu.");


        List<Recipe> sortedRecipeList = Arrays.asList(recipe2, recipe1);
        Page<Recipe> sortedRecipesPage = new PageImpl<>(sortedRecipeList);

        Mockito.when(recipeRepositoryMock.findRecipesBySearchText(Mockito.eq("kurczak"), Mockito.any(Pageable.class)))
                .thenReturn(sortedRecipesPage);

        int pageNumber = 1;
        int pageSize = 4;
        String sortField = "creationDate";

        //when
        Page<RecipeMainInfoDto> mainInfoDtoPage = recipeService.findRecipesByText("kurczak", pageNumber, pageSize, sortField);

        //then
        assertThat(mainInfoDtoPage.getContent().size(), is(2));
        assertThat(mainInfoDtoPage.getContent().get(0).getName(), is("Burczkowa"));
        assertThat(mainInfoDtoPage.getContent().get(1).getName(), is("Pomidorowa"));
    }

    @Test
    void shouldReturnEmptyPageWhenNoRecipesFoundByText() {
        //given

        Mockito.when(recipeRepositoryMock.findRecipesBySearchText(Mockito.eq("burger"), Mockito.any(Pageable.class)))
                .thenReturn(Page.empty());

        int pageNumber = 1;
        int pageSize = 4;
        String sortField = "creationDate";

        //when
        Page<RecipeMainInfoDto> mainInfoDtoPage = recipeService.findRecipesByText("burger", pageNumber, pageSize, sortField);

        //then
        assertThat(mainInfoDtoPage.getContent().size(), is(0));
        assertNotNull(mainInfoDtoPage);
    }

    @Test
    void shouldFindTwoRecipesByTextAndSortResultByName() {
        //given
        Type type = new Type();
        type.setId(11L);
        type.setName("Zupa");

        DifficultyLevel difficultyLevel = new DifficultyLevel();
        difficultyLevel.setId(21L);
        difficultyLevel.setName("Trudne");

        Recipe recipe1 = new Recipe();
        recipe1.setName("Agrestowa");
        recipe1.setType(type);
        recipe1.setCreationDate(LocalDateTime.now().minusDays(1));
        recipe1.setIngredients("kurczak\\ncurry\\ncebula\\\\nmleko kokosowe\\nprzyprawy");
        recipe1.setDirections("Podsmaż cebulę i czosnek.\\nDodaj kurczaka i curry.\\nWlej mleko kokosowe.\\nGotuj na wolnym ogniu.");

        Recipe recipe2 = new Recipe();
        recipe2.setName("Burczkowa");
        recipe2.setType(type);
        recipe2.setCreationDate(LocalDateTime.now());
        recipe2.setIngredients("kurczak\\ncurry\\ncebula\\\\nmleko kokosowe\\nprzyprawy");
        recipe2.setDirections("Podsmaż cebulę i czosnek.\\nDodaj kurczaka i curry.\\nWlej mleko kokosowe.\\nGotuj na wolnym ogniu.");


        List<Recipe> sortedRecipeList = Arrays.asList(recipe2, recipe1);
        Page<Recipe> sortedRecipesPage = new PageImpl<>(sortedRecipeList);

        Mockito.when(recipeRepositoryMock.findRecipesBySearchText(Mockito.eq("kurczak"), Mockito.any(Pageable.class)))
                .thenReturn(sortedRecipesPage);

        int pageNumber = 1;
        int pageSize = 4;
        String sortField = "name";

        //when
        Page<RecipeMainInfoDto> mainInfoDtoPage = recipeService.findRecipesByText("kurczak", pageNumber, pageSize, sortField);

        //then
        assertThat(mainInfoDtoPage.getContent().size(), is(2));
        assertThat(mainInfoDtoPage.getContent().get(0).getName(), is("Burczkowa"));
        assertThat(mainInfoDtoPage.getContent().get(1).getName(), is("Agrestowa"));
    }

    @Test
    void shouldReturnCorrectCountWhenUserHasTwoFavouriteRecipes() {
        //given
        Recipe recipe1 = new Recipe();
        recipe1.setName("Agrestowa");

        Recipe recipe2 = new Recipe();
        recipe2.setName("Burczkowa");

        Set<Recipe> favoriteRecipes = new HashSet<>();
        favoriteRecipes.add(recipe1);
        favoriteRecipes.add(recipe2);

        User user = new User();
        user.setEmail("john@mail.com");
        user.setFavoriteRecipes(favoriteRecipes);

        List<Recipe> favouriteRecipeList = Arrays.asList(recipe2, recipe1);

        Mockito.when(recipeRepositoryMock.findAllFavouritesRecipesForUser("john@mail.com")).thenReturn(favouriteRecipeList);

        //when
        long countFavouriteUserRecipes = recipeService.countFavouriteUserRecipes("john@mail.com");

        //then
        assertEquals(2, countFavouriteUserRecipes);
    }

    @Test
    void shouldReturnZeroWhenUserHasNoFavouriteRecipes() {
        //given
        User user = new User();
        user.setEmail("john@mail.com");

        Mockito.when(recipeRepositoryMock.findAllFavouritesRecipesForUser("john@mail.com")).thenReturn(Collections.emptyList());

        //when
        long countFavouriteUserRecipes = recipeService.countFavouriteUserRecipes("john@mail.com");

        //then
        assertEquals(0, countFavouriteUserRecipes);
    }

    @Test
    void shouldFindTwoRatedRecipesForUserSortingByName() {
        //given
        Type type = new Type();
        type.setId(11L);
        type.setName("Zupa");

        DifficultyLevel difficultyLevel = new DifficultyLevel();
        difficultyLevel.setId(21L);
        difficultyLevel.setName("Trudne");

        Recipe recipe1 = new Recipe();
        recipe1.setName("Agrestowa");
        recipe1.setType(type);
        recipe1.setCreationDate(LocalDateTime.now().minusDays(1));

        Recipe recipe2 = new Recipe();
        recipe2.setName("Burczkowa");
        recipe2.setType(type);
        recipe2.setCreationDate(LocalDateTime.now());

        User user = new User();
        user.setEmail("john@mail.com");

        Rating rating1 = new Rating();
        rating1.setUser(user);
        rating1.setRecipe(recipe1);

        Rating rating2 = new Rating();
        rating2.setUser(user);
        rating2.setRecipe(recipe2);

        List<Recipe> ratedRecipeList = Arrays.asList(recipe2, recipe1);

        Page<Recipe> recipePage = new PageImpl<>(ratedRecipeList);

        Mockito.when(recipeRepositoryMock.findAllRatedRecipesByUser(Mockito.eq("john@mail.com"), Mockito.any(Pageable.class))).thenReturn(recipePage);

        int pageNumber = 1;
        int pageSize = 4;
        String sortField = "name";

        //when
        Page<RecipeMainInfoDto> ratedRecipesForUser = recipeService.findRatedRecipesByUser("john@mail.com", pageNumber, pageSize, sortField);

        //then
        assertNotNull(ratedRecipesForUser);
        assertThat(ratedRecipesForUser.getTotalElements(), is(2L));
        assertThat(ratedRecipesForUser.getContent().get(0).getName(), is("Burczkowa"));
        assertThat(ratedRecipesForUser.getContent().get(1).getName(), is("Agrestowa"));
    }

    @Test
    void shouldReturnCorrectCountWhenUserHasTwoRatedRecipes() {
        //given
        Recipe recipe1 = new Recipe();
        recipe1.setName("Agrestowa");

        Recipe recipe2 = new Recipe();
        recipe2.setName("Burczkowa");

        User user = new User();
        user.setEmail("john@mail.com");

        Rating rating1 = new Rating();
        rating1.setUser(user);
        rating1.setRecipe(recipe1);

        Rating rating2 = new Rating();
        rating2.setUser(user);
        rating2.setRecipe(recipe2);

        List<Recipe> ratedRecipeList = Arrays.asList(recipe2, recipe1);

        Mockito.when(recipeRepositoryMock.findAllRatedRecipesByUser("john@mail.com")).thenReturn(ratedRecipeList);

        //when
        long ratedRecipeByUser = recipeService.countRatedRecipeByUser("john@mail.com");

        //then
        assertEquals(2, ratedRecipeByUser);
    }

    @Test
    void shouldReturnCorrectCountWhenUserHasNoRatedRecipes() {
        //given
        User user = new User();
        user.setEmail("john@mail.com");

        Mockito.when(recipeRepositoryMock.findAllRatedRecipesByUser("john@mail.com")).thenReturn(Collections.emptyList());

        //when
        long ratedRecipeByUser = recipeService.countRatedRecipeByUser("john@mail.com");

        //then
        assertEquals(0, ratedRecipeByUser);
    }

    @Test
    void updateRecipe() {
    }

    @Test
    void deleteRecipe() {
    }
}