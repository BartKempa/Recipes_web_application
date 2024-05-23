package com.example.recipes.web;

import com.example.recipes.domain.type.TypeService;
import com.example.recipes.domain.type.dto.TypeDto;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {
    private final TypeService typeService;

    public GlobalControllerAdvice(TypeService typeService) {
        this.typeService = typeService;
    }

    @ModelAttribute("types")
    public List<TypeDto> getTypeList(){
        return typeService.findAllTypes();
    }
}
