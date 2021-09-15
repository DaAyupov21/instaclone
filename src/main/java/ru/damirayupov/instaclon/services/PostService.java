package ru.damirayupov.instaclon.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.damirayupov.instaclon.dto.PostDto;
import ru.damirayupov.instaclon.exceptions.PostNotFoundException;
import ru.damirayupov.instaclon.models.ImageModel;
import ru.damirayupov.instaclon.models.Post;
import ru.damirayupov.instaclon.models.User;
import ru.damirayupov.instaclon.repositories.ImageRepository;
import ru.damirayupov.instaclon.repositories.PostRepository;
import ru.damirayupov.instaclon.repositories.UserRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    public Post createPost(PostDto postDto, Principal principal){
        User user = getUserByPrincipal(principal);
        Post post = Post.builder()
                .user(user)
                .caption(postDto.getCaption())
                .location(postDto.getLocation())
                .title(postDto.getTitle())
                .likes(0)
                .build();

         log.info("Saving post for User: {}", user.getUsername());

        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreateAtDesc();
    }

    public Post getPostById(Long postId, Principal principal){
        User user = getUserByPrincipal(principal);
        return postRepository.findPostByIdAndUser(postId, user).orElseThrow(
                () -> new PostNotFoundException("Post cannot be found for username: " +  user.getUsername()));
    }

    public List<Post> getAllPostsByUser(Principal principal){
        User user = getUserByPrincipal(principal);
        return postRepository.findAllByUserOrderByCreateAtDesc(user);
    }

    public Post likePost(Long postId, String username){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found"));
        Optional<String> userLiked = post.getLikesUsers()
                .stream().filter(u -> u.equals(username)).findAny();

        if (userLiked.isPresent()) {
            post.setLikes(post.getLikes() - 1);
            post.getLikesUsers().remove(username);
        } else {
            post.setLikes(post.getLikes() + 1);
            post.getLikesUsers().add(username);
        }
        return postRepository.save(post);
    }

    public void deletePost(Long postId, Principal principal){
        Post post = getPostById(postId, principal);
        Optional<ImageModel> imageModel = imageRepository.findByPostId(post.getId());
        postRepository.delete(post);
        imageModel.ifPresent(model -> imageRepository.delete(model));
    }

    private User getUserByPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username not found " + username));

    }
}
