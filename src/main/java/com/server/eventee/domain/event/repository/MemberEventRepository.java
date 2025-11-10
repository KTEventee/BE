package com.server.eventee.domain.event.repository;

import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.event.model.MemberEvent;
import com.server.eventee.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberEventRepository extends JpaRepository<MemberEvent, Long> {

  List<MemberEvent> findAllByMemberAndIsDeletedFalse(Member member);

  List<MemberEvent> findAllByEventAndIsDeletedFalse(Event event);

  boolean existsByMemberAndEventAndIsDeletedFalse(Member member, Event event);

  Optional<MemberEvent> findByMemberAndEventAndIsDeletedFalse(Member member, Event event);
}
