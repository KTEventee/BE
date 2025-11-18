package com.server.eventee.domain.group.repository;

import com.server.eventee.domain.group.model.Group;
import com.server.eventee.domain.group.model.MemberGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberGroupRepository extends JpaRepository<MemberGroup,Long> {
    List<MemberGroup> findMemberGroupsByGroup(Group group);
}
