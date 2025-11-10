package com.server.eventee.domain.event.repository;

import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.event.model.MemberEvent;
import com.server.eventee.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberEventRepository extends JpaRepository<MemberEvent, Long> {

  List<MemberEvent> findAllByMemberAndIsDeletedFalse(Member member);

  List<MemberEvent> findAllByEventAndIsDeletedFalse(Event event);

  boolean existsByMemberAndEventAndIsDeletedFalse(Member member, Event event);
}
