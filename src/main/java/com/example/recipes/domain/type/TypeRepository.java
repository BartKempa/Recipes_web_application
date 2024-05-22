package com.example.recipes.domain.type;

import com.example.recipes.domain.type.Type;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface TypeRepository extends CrudRepository<Type, Long> {
    Optional<Type> findByNameIgnoreCase(String name);
}
