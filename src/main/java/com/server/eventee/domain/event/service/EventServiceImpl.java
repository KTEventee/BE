package com.server.eventee.domain.event.service;

import com.server.eventee.domain.event.converter.EventConverter;
import com.server.eventee.domain.event.dto.EventRequest;
import com.server.eventee.domain.event.dto.EventResponse;
import com.server.eventee.domain.event.excepiton.EventHandler;
import com.server.eventee.domain.event.excepiton.status.EventErrorStatus;
import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.event.model.MemberEvent;
import com.server.eventee.domain.event.repository.EventRepository;
import com.server.eventee.domain.event.repository.MemberEventRepository;
import com.server.eventee.domain.group.model.Group;
import com.server.eventee.domain.group.repository.GroupRepository;
import com.server.eventee.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;
  private final MemberEventRepository memberEventRepository;
  private final GroupRepository groupRepository;
  private final EventConverter eventConverter;

  @Transactional
  @Override
  public EventResponse.CreateResponse createEvent(Member member, EventRequest.CreateRequest request) {
    if (request.teamCount() == null || request.teamCount() <= 0) {
      throw new EventHandler(EventErrorStatus.EVENT_TEAM_COUNT_INVALID);
    }

    String inviteCode = RandomStringUtils.randomAlphabetic(6).toUpperCase();
    Event event = eventConverter.toEvent(inviteCode, request.title(), request.description(),
        request.startAt(), request.endAt(), request.teamCount());
    eventRepository.save(event);

    MemberEvent hostRelation = eventConverter.toHostRelation(member, event);
    memberEventRepository.save(hostRelation);

    for (int i = 1; i <= request.teamCount(); i++) {
      Group group = eventConverter.toGroup(i, member, event);
      groupRepository.save(group);
    }

    return eventConverter.toCreateResponse(event, member);
  }

  @Transactional
  @Override
  public EventResponse.JoinResponse joinEvent(Member member, String inviteCode) {
    Event event = eventRepository.findByInviteCode(inviteCode)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.INVITE_CODE_NOT_FOUND));

    if ("CLOSED".equalsIgnoreCase(event.getStatus())) {
      throw new EventHandler(EventErrorStatus.EVENT_CLOSED);
    }

    MemberEvent memberEvent = memberEventRepository.findByMemberAndEventAndIsDeletedFalse(member, event)
        .orElseGet(() -> {
          MemberEvent newJoin = eventConverter.toParticipantRelation(member, event);
          return memberEventRepository.save(newJoin);
        });

    return eventConverter.toJoinResponse(event, memberEvent);
  }
}
