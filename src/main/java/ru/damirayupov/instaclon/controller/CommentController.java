package ru.damirayupov.instaclon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.damirayupov.instaclon.dto.CommentDto;
import ru.damirayupov.instaclon.models.Comment;
import ru.damirayupov.instaclon.payload.response.MessageResponse;
import ru.damirayupov.instaclon.services.CommentService;
import ru.damirayupov.instaclon.validations.ResponseErrorValidation;

import static ru.damirayupov.instaclon.dto.CommentDto.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @PostMapping("/{post_id}/create")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @PathVariable("post_id") String postId,
                                                BindingResult bindingResult,
                                                Principal principal){
        ResponseEntity<Object> errors = getErrorsResponseEntity(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;
        Comment comment = commentService.saveComment(Long.parseLong(postId), commentDto, principal);
        return ResponseEntity.ok(from(comment));
    }

    @GetMapping("/{post_id}/all")
    public ResponseEntity<List<CommentDto>> getAllCommentsToPost(@PathVariable("post_id") String postId){
        return ResponseEntity.ok(from(commentService.getAllCommentsForPost(Long.parseLong(postId))));
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable("comment_id") String commentId) {
        commentService.deleteComment(Long.parseLong(commentId));
        return ResponseEntity.ok(new MessageResponse("Comment was deleted"));
    }

    private ResponseEntity<Object> getErrorsResponseEntity(BindingResult bindingResult) {
        return responseErrorValidation.mapValidationService(bindingResult);
    }
}
