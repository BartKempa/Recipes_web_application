package com.example.recipes.domain.type;

import com.example.recipes.domain.type.dto.TypeDto;

class TypeDtoMapper {
    static TypeDto map(Type type){
        return new TypeDto(
                type.getId(),
                type.getName()
        );
    }
}
