package com.server.eventee.domain.group.repository;


import com.server.eventee.domain.group.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group,Long> {
    Optional<Group> findGroupByGroupId(Long groupId);
}
