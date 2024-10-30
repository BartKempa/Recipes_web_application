package com.example.recipes.domain.type;

import com.example.recipes.domain.recipe.Recipe;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

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
}
