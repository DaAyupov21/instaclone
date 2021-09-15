package ru.damirayupov.instaclon.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.damirayupov.instaclon.models.Post;
import ru.damirayupov.instaclon.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserOrderByCreateAtDesc(User user);
    List<Post> findAllByOrderByCreateAtDesc();
    Optional<Post> findPostByIdAndUser(Long id, User user);
}
