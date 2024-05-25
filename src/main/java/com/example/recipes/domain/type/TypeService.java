package com.example.recipes.domain.type;

import com.example.recipes.domain.type.dto.TypeDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

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

    public List<TypeDto> findAllTypes(){
        return StreamSupport.stream(typeRepository.findAll().spliterator(), false)
                .map(TypeDtoMapper::map)
                .toList();
    }

    @Transactional
    public void addType(TypeDto typeDto){
        Type type = new Type();
        type.setName(typeDto.getName());
        typeRepository.save(type);
    }
}
