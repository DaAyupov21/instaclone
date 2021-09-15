package ru.damirayupov.instaclon.dto;

import lombok.Builder;
import lombok.Data;
import ru.damirayupov.instaclon.models.Comment;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class CommentDto {

    private Long id;
    @NotEmpty
    private String message;
    private String username;

    public static CommentDto from (Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .username(comment.getUsername())
                .build();
    }

    public static List<CommentDto> from (List<Comment> comments) {
        return comments.stream().map(CommentDto::from).collect(Collectors.toList());
    }
}
