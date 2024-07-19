package com.example.recipes.domain.type;

import com.example.recipes.domain.type.dto.TypeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Page<TypeDto> findPaginatedTypesList(int pageNumber, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        return typeRepository.findAll(pageable)
                .map(TypeDtoMapper::map);
    }

    @Transactional
    public void addType(TypeDto type){
        Type typeToSave = new Type();
        typeToSave.setName(type.getName());
        typeRepository.save(typeToSave);
    }
}
