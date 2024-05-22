package com.example.recipes.domain.type.dto;

import com.example.recipes.domain.type.Type;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TypeRepository extends CrudRepository<Type, Long> {
    Optional<Type> findByNameIgnoreCase(String name);
}
