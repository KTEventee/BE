package com.server.eventee.domain.post.repository;

import com.server.eventee.domain.group.model.Group;
import com.server.eventee.domain.post.model.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {
    Optional<Post> findPostByPostId(Long id);

    List<Post> findAllByGroupAndIsDeletedFalse(Group group);
}
