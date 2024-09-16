package com.example.recipes.web;

import com.example.recipes.domain.recipe.RecipeService;
import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.type.TypeService;
import com.example.recipes.domain.type.dto.TypeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TypeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TypeService typeService;

    @Autowired
    private RecipeService recipeService;

    @Test
    @Transactional
    void shouldGetRecipesByType() throws Exception {
        //given
        String typeName = "ryby";
        int pageNo = 1;
        int pageSize = 6;
        TypeDto typeDto = typeService.findTypeByName(typeName).orElseThrow();
        Page<RecipeMainInfoDto> recipesByType = recipeService.findRecipesByType(typeName, pageNo, pageSize);
        int totalPages = recipesByType.getTotalPages();

        //when
        //then
         mockMvc.perform(get("/typ/{typeName}/strona/{pageNo}", typeName, pageNo)
                        .with(csrf()))
                 .andDo(print())
                 .andExpect(status().isOk())
                 .andExpect(view().name("recipe-listing"))
                 .andExpect(model().attribute("totalPages", totalPages))
                 .andExpect(model().attribute("currentPage", pageNo))
                 .andExpect(model().attribute("heading", typeDto.getName()))
                 .andExpect(model().attribute("baseUrl", "/typ/" + typeName + "/strona"));
    }
}