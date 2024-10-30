package com.example.recipes.domain.type;

import com.example.recipes.domain.type.Type;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TypeRepository extends CrudRepository<Type, Long>, PagingAndSortingRepository<Type, Long> {
    Optional<Type> findByNameIgnoreCase(String name);
}
