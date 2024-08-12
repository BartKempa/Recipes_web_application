package com.example.recipes.domain.type;

import com.example.recipes.domain.comment.CommentRepository;
import com.example.recipes.domain.rating.RatingRepository;
import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
import com.example.recipes.domain.type.dto.TypeDto;
import com.example.recipes.domain.user.User;
import com.example.recipes.domain.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class TypeService {
    private final TypeRepository typeRepository;
    private final RecipeRepository recipeRepository;
    private final RatingRepository ratingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public TypeService(TypeRepository typeRepository, RecipeRepository recipeRepository, RatingRepository ratingRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.typeRepository = typeRepository;
        this.recipeRepository = recipeRepository;
        this.ratingRepository = ratingRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
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


    @Transactional
    public void deleteType(long typeId) {
        Type typeToDelete = typeRepository.findById(typeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<Recipe> recipesToDelete = recipeRepository.findAllByType_Id(typeId);
        for (Recipe recipe : recipesToDelete) {
            for (User user : recipe.getFavourites()) {
                boolean removed = user.getFavoriteRecipes().remove(recipe);
                System.out.println("Recipe removed: " + removed);
                userRepository.save(user);
            }
        }
        for (Recipe recipe : recipesToDelete) {
                ratingRepository.deleteAll(recipe.getRatings());
                commentRepository.deleteAll(recipe.getComments());
            }
        recipeRepository.deleteAllByType_Id(typeId);
        typeRepository.delete(typeToDelete);
    }

    @Transactional
    public void updateType(TypeDto typeDto){
        Type typeToUpdate = typeRepository.findById(typeDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        typeToUpdate.setName(typeDto.getName());
        typeRepository.save(typeToUpdate);
    }


    public Optional<TypeDto> findTypeById(long typeId){
        return typeRepository.findById(typeId)
                .map(TypeDtoMapper::map);
    }
}



