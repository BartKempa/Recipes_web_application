package com.example.recipes.domain.comment;

import com.example.recipes.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findAllByRecipeId(long recipeId);

    @Query("SELECT c FROM Comment c JOIN c.user u WHERE u.email =:email")
    Page<Comment> findAllUserComments(String email, Pageable pageable);

}
