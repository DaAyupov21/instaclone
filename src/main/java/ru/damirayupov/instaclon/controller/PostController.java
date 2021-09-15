package ru.damirayupov.instaclon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.damirayupov.instaclon.dto.PostDto;
import ru.damirayupov.instaclon.models.Post;
import ru.damirayupov.instaclon.payload.response.MessageResponse;
import ru.damirayupov.instaclon.services.PostService;
import ru.damirayupov.instaclon.validations.ResponseErrorValidation;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static ru.damirayupov.instaclon.dto.PostDto.from;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @PostMapping("/create")
    public ResponseEntity<Object> createPost(@Valid @RequestBody PostDto postDto,
                                             BindingResult bindingResult,
                                             Principal principal) {
        ResponseEntity<Object> errors = getErrorsResponseEntity(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;

        PostDto createdPost = from(postService.createPost(postDto, principal));
        return ResponseEntity.ok(createdPost);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostDto>> getAllPosts() {
        return ResponseEntity.ok(from(postService.getAllPosts()));
    }

    @GetMapping("/user/posts")
    public ResponseEntity<List<PostDto>> getAllPostsUser(Principal principal) {
        return ResponseEntity.ok(from(postService.getAllPostsByUser(principal)));
    }

    @PostMapping("/{post_id}/{username}/like")
    public ResponseEntity<PostDto> likePost(@PathVariable("post_id") String postId,
                                            @PathVariable("username") String username) {
        Post post = postService.likePost(Long.parseLong(postId), username);
        return ResponseEntity.ok(from(post));
    }

    @DeleteMapping("/post_id") //вместо post запроса
    public ResponseEntity<MessageResponse> deletePost(@PathVariable("post_id") String postId,
                                                      Principal principal) {
        postService.deletePost(Long.parseLong(postId), principal);
        return ResponseEntity.ok(new MessageResponse("Post was Deleted"));
    }


    private ResponseEntity<Object> getErrorsResponseEntity(BindingResult bindingResult) {
        return responseErrorValidation.mapValidationService(bindingResult);
    }
}
