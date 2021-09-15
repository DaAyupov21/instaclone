package ru.damirayupov.instaclon.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.damirayupov.instaclon.models.Comment;
import ru.damirayupov.instaclon.models.Post;

import java.util.List;
import java.util.Optional;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost(Post post);
    Optional<Comment> findByIdAndUserId (Long commentId, Long userId);

}
