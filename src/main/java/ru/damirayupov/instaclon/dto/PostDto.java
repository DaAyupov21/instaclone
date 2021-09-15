package ru.damirayupov.instaclon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.damirayupov.instaclon.models.Post;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {

    private Long id;
    private String title;
    private String caption;
    private String location;
    private String username;
    private Integer likes;
    private Set<String> usersLikes;

    public static PostDto from(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .caption(post.getCaption())
                .location(post.getLocation())
                .username(post.getUser().getUsername())
                .likes(post.getLikes())
                .usersLikes(post.getLikesUsers())
                .build();
    }

    public static List<PostDto> from (List<Post> posts){
        return posts.stream().map(PostDto::from).collect(Collectors.toList());
    }
}
