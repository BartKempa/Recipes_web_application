package com.example.recipes.domain.type;

import com.example.recipes.domain.type.dto.TypeDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TypeService {
    private final TypeRepository typeRepository;

    public TypeService(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    public Optional<TypeDto> findTypeByName(String name){
        return typeRepository.findByNameIgnoreCase(name)
                .map(TypeDtoMapper::map);
    }
}
