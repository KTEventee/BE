package com.server.eventee.domain.event.repository;

import com.server.eventee.domain.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

  Optional<Event> findByInviteCode(String inviteCode);
  Optional<Event> findByIdAndIsDeletedFalse(Long id);
}
