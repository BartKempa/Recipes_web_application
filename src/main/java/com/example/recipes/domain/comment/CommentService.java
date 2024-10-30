package com.example.recipes.domain.comment;

import com.example.recipes.domain.comment.dto.CommentDto;
import com.example.recipes.domain.recipe.Recipe;
import com.example.recipes.domain.recipe.RecipeRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final CommentDateTimeProvider dateTimeProvider;

    CommentService(CommentRepository commentRepository, UserRepository userRepository, RecipeRepository recipeRepository, CommentDateTimeProvider dateTimeProvider) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    public List<CommentDto> getActiveCommentsForRecipe(long id) {
        return commentRepository.findAllByRecipeId(id).stream()
                .map(CommentDtoMapper::map)
                .filter(CommentDto::isApproved)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addComment(CommentDto commentDto, long recipeId, String userName) {
        Comment commentToSave = new Comment();
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
        User user = userRepository.findByEmail(userName).orElseThrow();
        LocalDateTime date = dateTimeProvider.getCurrentTime();
        commentToSave.setId(commentDto.getId());
        commentToSave.setCreationDate(date);
        commentToSave.setApproved(false);
        commentToSave.setText(commentDto.getText());
        commentToSave.setRecipe(recipe);
        recipe.getComments().add(commentToSave);
        commentToSave.setUser(user);
        commentRepository.save(commentToSave);
    }

    public Page<CommentDto> findAllUserComments(String email, int pageNumber, int pageSize, String sortField) {
        Sort sort = Sort.by(sortField).descending();
        Pageable pageable = getPageable(pageNumber, pageSize, sort);
        return commentRepository.findAllUserComments(email, pageable)
                .map(CommentDtoMapper::map);
    }

    private Pageable getPageable(int pageNumber, int pageSize, Sort sort) {
        return PageRequest.of(pageNumber - 1, pageSize, sort);
    }

    public long countUserComments(String email) {
        List<Comment> allUserComments = commentRepository.findAllUserComments(email);
        return allUserComments.size();
    }

    @Transactional
    public void deleteComment(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        commentRepository.delete(comment);
    }

    public Page<CommentDto> findPaginatedCommentsList(int pageNumber, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = getPageable(pageNumber, pageSize, sort);
        return commentRepository.findAll(pageable)
                .map(CommentDtoMapper::map);
    }

    @Transactional
    public void approveComment(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        comment.setApproved(true);
        commentRepository.save(comment);
    }

    public Optional<CommentDto> findCommentById(long commentId) {
        return commentRepository.findById(commentId).map(CommentDtoMapper::map);
    }

    @Transactional
    public void updateComment(CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        comment.setText(commentDto.getText());
        comment.setApproved(false);
        commentRepository.save(comment);
    }
}
