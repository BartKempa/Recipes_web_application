package com.example.recipes.web.admin;

import com.example.recipes.domain.type.TypeService;
import com.example.recipes.domain.type.dto.TypeDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TypeManagementController {
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
    public String addType(TypeDto type, RedirectAttributes redirectAttributes){
        typeService.addType(type);
        redirectAttributes.addFlashAttribute(
                AdminController.ADMIN_NOTIFICATION_ATTRIBUTE,
                ("Typ posiłku %s został zapisany").formatted(type.getName()));
        return "redirect:/admin";
    }
}
