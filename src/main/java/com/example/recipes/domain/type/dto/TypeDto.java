package com.example.recipes.domain.type.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class TypeDto {
    private Long id;
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    public TypeDto() {
    }

    public TypeDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeDto typeDto)) return false;
        return Objects.equals(id, typeDto.id) && Objects.equals(name, typeDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
