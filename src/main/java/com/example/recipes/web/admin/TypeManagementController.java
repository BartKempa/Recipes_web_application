package com.example.recipes.web.admin;

import com.example.recipes.domain.recipe.dto.RecipeMainInfoDto;
import com.example.recipes.domain.type.TypeService;
import com.example.recipes.domain.type.dto.TypeDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class TypeManagementController {
    public final static int PAGE_SIZE = 6;
    private final TypeService typeService;

    public TypeManagementController(TypeService typeService) {
        this.typeService = typeService;
    }

    @GetMapping("/admin/dodaj-typ")
    public String addTypeForm(Model model){
        TypeDto type = new TypeDto();
        model.addAttribute("type", type);
        return "admin/type-form";
    }

    @PostMapping("/admin/dodaj-typ")
    public String addType(@Valid @ModelAttribute("type") TypeDto type,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()){
            return "admin/type-form";
        } else {
            typeService.addType(type);
            redirectAttributes.addFlashAttribute(
                    AdminController.ADMIN_NOTIFICATION_ATTRIBUTE,
                    ("Typ posiłku %s został zapisany").formatted(type.getName()));
            return "redirect:/admin";
        }
    }

    @GetMapping("/admin/lista-typow/{pageNo}")
    public String getTypesList(
            @PathVariable Optional<Integer> pageNo,
            @RequestParam (value = "sortDir", defaultValue = "asc") String sortDir,
            Model model){
        int pageNumber = pageNo.orElse(1);
        String sorField = "name";
        Page<TypeDto> typesPage = typeService.findPaginatedTypesList(pageNumber, PAGE_SIZE, sorField, sortDir);
        List<TypeDto> types = typesPage.getContent();
        int totalPages = typesPage.getTotalPages();
        model.addAttribute("types", types);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("heading", "Lista typów posiłków");
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sorField);
        model.addAttribute("baseUrl", "/admin/lista-typow");
        return "admin/admin-type-list";
    }

    @PostMapping("/admin/usun-typ")
    public String deleteType(
            @RequestParam(value = "id") long id,
            @RequestHeader String referer){
        typeService.deleteType(id);
        return "redirect:" + referer;
    }

    @GetMapping("/admin/aktualizuj-typ/{typeId}")
    public String updateTypeForm(
            @PathVariable long typeId,
            Model model){
        Optional<TypeDto> type = typeService.findTypeById(typeId);
        model.addAttribute("type", type);
        return "admin/update-type";
    }

    @PostMapping("/admin/aktualizuj-typ")
    public String updateType(
            @Valid @ModelAttribute("type") TypeDto type,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()){
            return "admin/update-type";
        } else {
            typeService.updateType(type);
            redirectAttributes.addFlashAttribute(AdminController.ADMIN_NOTIFICATION_ATTRIBUTE,
                    "Zaktualizowano typ posiłku - %s".formatted(type.getName()));
            return "redirect:/admin";
        }
    }
}
