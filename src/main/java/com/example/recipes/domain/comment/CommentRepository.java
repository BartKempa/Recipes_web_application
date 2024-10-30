package com.example.recipes.domain.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long>, PagingAndSortingRepository<Comment, Long> {
    List<Comment> findAllByRecipeId(long recipeId);

    @Query("SELECT c FROM Comment c JOIN c.user u WHERE u.email =:email")
    Page<Comment> findAllUserComments(String email, Pageable pageable);

    @Query("SELECT c FROM Comment c JOIN c.user u WHERE u.email =:email")
    List<Comment> findAllUserComments(String email);
}
