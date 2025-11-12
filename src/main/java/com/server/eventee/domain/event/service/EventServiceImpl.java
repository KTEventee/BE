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
import com.server.eventee.domain.post.model.Post;
import com.server.eventee.domain.post.repository.PostRepository;
import java.util.List;
import java.util.Objects;
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
  private final PostRepository postRepository;
  private final EventConverter eventConverter;

  @Transactional
  @Override
  public EventResponse.CreateResponse createEvent(Member member, EventRequest.CreateRequest request) {
    if (request.teamCount() == null || request.teamCount() <= 0) {
      throw new EventHandler(EventErrorStatus.EVENT_TEAM_COUNT_INVALID);
    }

    String inviteCode = RandomStringUtils.randomAlphabetic(6).toUpperCase();

    Event event = eventConverter.toEvent(
        inviteCode,
        request.title(),
        request.description(),
        request.password(),
        request.startAt(),
        request.endAt(),
        request.teamCount()
    );
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
  public EventResponse.JoinResponse joinEvent(Member member, EventRequest.JoinRequest request) {
    Event event = eventRepository.findByInviteCode(request.inviteCode())
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    if (!Objects.equals(event.getPassword(), request.password())) {
      throw new EventHandler(EventErrorStatus.EVENT_PASSWORD_INVALID);
    }

    MemberEvent memberEvent = memberEventRepository
        .findByMemberAndEventAndIsDeletedFalse(member, event)
        .orElse(null);

    if (memberEvent == null) {
      memberEvent = MemberEvent.builder()
          .member(member)
          .event(event)
          .role(MemberEvent.MemberEventRole.PARTICIPANT)
          .nickname(request.nickname())
          .build();
      memberEventRepository.save(memberEvent);
    } else {
      memberEvent.updateNickname(request.nickname());
    }

    return eventConverter.toJoinResponse(event, memberEvent);
  }

  @Transactional(readOnly = true)
  @Override
  public EventResponse.EventWithGroupsResponse getEventGroups(Member member, Long eventId) {
    // 이벤트 존재 여부 검증
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    // 로그인한 사용자가 이벤트 참여자인지 검증
    boolean isParticipant = memberEventRepository.existsByMemberAndEventAndIsDeletedFalse(member, event);
    if (!isParticipant) {
      throw new EventHandler(EventErrorStatus.EVENT_ACCESS_DENIED);
    }

    // 그룹 목록 조회
    List<Group> groups = groupRepository.findAllByEventId(eventId);

    // Converter를 이용해 DTO 변환
    return eventConverter.toEventWithGroupsResponse(event, groups);
  }

  @Transactional(readOnly = true)
  @Override
  public EventResponse.GroupPostsResponse getGroupPosts(Member member, Long eventId, Long groupId) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    boolean isParticipant = memberEventRepository.existsByMemberAndEventAndIsDeletedFalse(member, event);
    if (!isParticipant) {
      throw new EventHandler(EventErrorStatus.EVENT_ACCESS_DENIED);
    }

    Group group = groupRepository.findGroupByGroupId(groupId)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.GROUP_NOT_FOUND));

    if (!Objects.equals(group.getEvent().getId(), eventId)) {
      throw new EventHandler(EventErrorStatus.GROUP_NOT_BELONGS_TO_EVENT);
    }

    List<Post> posts = postRepository.findAllByGroupAndIsDeletedFalse(group);

    return eventConverter.toGroupPostsResponse(group, posts);
  }


}
