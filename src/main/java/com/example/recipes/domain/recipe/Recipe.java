package com.example.recipes.domain.recipe;

import com.example.recipes.domain.type.Type;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private Type type;




}
