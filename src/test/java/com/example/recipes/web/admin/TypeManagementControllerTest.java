package com.example.recipes.web.admin;

import com.example.recipes.domain.type.TypeService;
import com.example.recipes.domain.type.dto.TypeDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TypeManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TypeService typeService;

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetAddTypeForm() throws Exception {
        mockMvc.perform(get("/admin/dodaj-typ")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/type-form"))
                .andExpect(model().attributeExists("type"))
                .andExpect(model().attribute("type", Matchers.instanceOf(TypeDto.class)));
    }

    @Test
    void shouldRedirectToLoginPageWhenUserNotAuthorizedAndTryGetAddTypeForm() throws Exception {
        mockMvc.perform(get("/admin/dodaj-typ")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAddType() throws Exception {
        //given
        final String typeName = "Pierogi";
        TypeDto typeDto = new TypeDto();
        typeDto.setName(typeName);

        //when
        mockMvc.perform(post("/admin/dodaj-typ")
                .flashAttr("type", typeDto)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        //then
        assertTrue(typeService.findTypeByName(typeName).isPresent());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldRejectTypeWithEmptyName() throws Exception {
        //given
        final String typeName = "";
        TypeDto typeDto = new TypeDto();
        typeDto.setName(typeName);

        //when
        //then
        mockMvc.perform(post("/admin/dodaj-typ")
                        .flashAttr("type", typeDto)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("type", "name"))
                .andExpect(view().name("admin/type-form"));
    }

    @Test
    void shouldRedirectToLoginWhenUserIsNotAuthenticated() throws Exception {
        //given
        final String typeName = "Pierogi";
        TypeDto typeDto = new TypeDto();
        typeDto.setName(typeName);

        //when
        mockMvc.perform(post("/admin/dodaj-typ")
                        .flashAttr("type", typeDto)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetTypesList() throws Exception {
        //given
        int pageNo = 1;
        String sortField = "name";
        String sortDir = "asc";
        Page<TypeDto> typesPage = typeService.findPaginatedTypesList(pageNo, TypeManagementController.PAGE_SIZE, sortField, sortDir);
        List<TypeDto> types = typesPage.getContent();
        //when
        mockMvc.perform(get("/admin/lista-typow/{pageNo}", pageNo)
                .param("sortDir", sortDir)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-type-list"))
                .andExpect(model().attributeExists("types"))
                .andExpect(model().attribute("types", types))
                .andExpect(model().attribute("totalPages", typesPage.getTotalPages()))
                .andExpect(model().attribute("currentPage", pageNo))
                .andExpect(model().attribute("sortField", sortField))
                .andExpect(model().attribute("heading", "Lista typów posiłków"))
                .andExpect(model().attribute("sortDir", sortDir))
                .andExpect(model().attribute("baseUrl", "/admin/lista-typow"));

        //then
        assertFalse(types.isEmpty());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldDeleteType() throws Exception {
        //given
        final long typeIdToDelete = 1L;
        final String referer = "/some-page";
        assertTrue(typeService.findTypeById(typeIdToDelete).isPresent());

        //when
        mockMvc.perform(post("/admin/usun-typ")
                .param("id", String.valueOf(typeIdToDelete))
                .header("referer", referer)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(referer));

        //then
        assertFalse(typeService.findTypeById(typeIdToDelete).isPresent());
    }

    @Test
    void shouldRedirectToLoginPageWhenTryDeleteTypeAndUserIsNotAuthenticated() throws Exception {
        //given
        final long typeIdToDelete = 1L;
        final String referer = "/some-page";
        assertTrue(typeService.findTypeById(typeIdToDelete).isPresent());

        //when
        //then
        mockMvc.perform(post("/admin/usun-typ")
                        .param("id", String.valueOf(typeIdToDelete))
                        .header("referer", referer)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetNotFoundStatusWhenDeleteNotExistsType() throws Exception {
        //given
        final long notExistsTypeNumber = 111L;
        final String referer = "/some-page";
        assertFalse(typeService.findTypeById(notExistsTypeNumber).isPresent());

        //when
        //then
        mockMvc.perform(post("/admin/usun-typ")
                        .param("id", String.valueOf(notExistsTypeNumber))
                        .header("referer", referer)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldGetUpdateTypeForm() throws Exception {
        //given
        final long typeIdToUpdate = 1L;
        assertTrue(typeService.findTypeById(typeIdToUpdate).isPresent());

        //when
        //then
        mockMvc.perform(get("/admin/aktualizuj-typ/{typeId}", typeIdToUpdate)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/update-type"));
    }

    @Test
    void shouldRedirectToLoginPageWhenUserNotAuthorizedAndTryGetUpdateTypeForm() throws Exception {
        //given
        final long typeIdToUpdate = 1L;
        assertTrue(typeService.findTypeById(typeIdToUpdate).isPresent());

        //when
        //then
        mockMvc.perform(get("/admin/aktualizuj-typ/{typeId}", typeIdToUpdate)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldUpdateType() throws Exception {
        //given
        final long typeIdToUpdate = 1L;
        TypeDto typeDto = new TypeDto();
        typeDto.setId(typeIdToUpdate);
        typeDto.setName("Pierogi");
        assertTrue(typeService.findTypeById(typeIdToUpdate).isPresent());

        //when
        mockMvc.perform(post("/admin/aktualizuj-typ")
                .param("id", String.valueOf(typeDto.getId()))
                .param("name", typeDto.getName())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        //then
        assertEquals(typeService.findTypeById(typeIdToUpdate).orElseThrow().getName(), typeDto.getName());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldReturnToUpdateFormWhenValidationFailed() throws Exception {
        //given
        final long typeIdToUpdate = 1L;
        TypeDto typeDtoBeforeUpdate = typeService.findTypeById(typeIdToUpdate).orElseThrow();
        TypeDto typeDto = new TypeDto();
        typeDto.setId(typeIdToUpdate);
        typeDto.setName("a");
        assertTrue(typeService.findTypeById(typeIdToUpdate).isPresent());

        //when
        //then
        mockMvc.perform(post("/admin/aktualizuj-typ")
                        .param("id", String.valueOf(typeDto.getId()))
                        .param("name", typeDto.getName())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/update-type"));
        //then
        TypeDto typeDtoAfterUpdate = typeService.findTypeById(typeIdToUpdate).orElseThrow();
        assertEquals(typeDtoBeforeUpdate.getName(), typeDtoAfterUpdate.getName());
    }
}