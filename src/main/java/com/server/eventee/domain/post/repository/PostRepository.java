package com.server.eventee.domain.post.repository;

import com.server.eventee.domain.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {
    Optional<Post> findPostByPostId(Long id);
}
