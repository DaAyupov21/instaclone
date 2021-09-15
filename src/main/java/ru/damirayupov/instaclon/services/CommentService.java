package ru.damirayupov.instaclon.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.damirayupov.instaclon.dto.CommentDto;
import ru.damirayupov.instaclon.exceptions.PostNotFoundException;
import ru.damirayupov.instaclon.models.Comment;
import ru.damirayupov.instaclon.models.Post;
import ru.damirayupov.instaclon.models.User;
import ru.damirayupov.instaclon.repositories.CommentRepository;
import ru.damirayupov.instaclon.repositories.PostRepository;
import ru.damirayupov.instaclon.repositories.UserRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    public Comment saveComment(Long postId, CommentDto commentDto, Principal principal) {
        User user = getUserByPrincipal(principal);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found for " + user.getUsername()));
        Comment comment = Comment.builder()
                .post(post)
                .userId(user.getId())
                .username(user.getUsername())
                .message(commentDto.getMessage())
                .build();

        log.info("Save comment for Post {}", postId);
        return commentRepository.save(comment);
    }

    public List<Comment> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found by id:" + postId));
        return commentRepository.findAllByPost(post);
    }

    public void deleteComment(Long commentId){
        Optional<Comment> comment = commentRepository.findById(commentId);
        comment.ifPresent(c -> commentRepository.delete(c));
    }

    private User getUserByPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username not found " + username));
    }
}
