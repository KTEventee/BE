package com.server.eventee.domain.event.service;

import com.server.eventee.domain.event.converter.EventConverter;
import com.server.eventee.domain.event.dto.EventRequest;
import com.server.eventee.domain.event.dto.EventResponse;
import com.server.eventee.domain.event.dto.MemberListDto;
import com.server.eventee.domain.event.excepiton.EventHandler;
import com.server.eventee.domain.event.excepiton.status.EventErrorStatus;
import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.event.model.MemberEvent;
import com.server.eventee.domain.event.model.MemberEvent.MemberEventRole;
import com.server.eventee.domain.event.repository.EventRepository;
import com.server.eventee.domain.event.repository.MemberEventRepository;
import com.server.eventee.domain.group.model.Group;
import com.server.eventee.domain.group.repository.GroupRepository;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.post.model.Post;
import com.server.eventee.domain.post.repository.PostRepository;

import java.util.List;
import java.util.Objects;

import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;
  private final MemberEventRepository memberEventRepository;
  private final GroupRepository groupRepository;
  private final PostRepository postRepository;
  private final EventConverter eventConverter;

  // 🎉 이벤트 생성
  @Transactional
  @Override
  public EventResponse.CreateResponse createEvent(Member member, EventRequest.CreateRequest request) {

    if (request.teamCount() == null || request.teamCount() <= 0) {
      throw new EventHandler(EventErrorStatus.EVENT_TEAM_COUNT_INVALID);
    }

    // 초대 코드 생성
    String inviteCode = RandomStringUtils.randomAlphabetic(6).toUpperCase();

    // 이벤트 엔티티 생성
    Event event = eventConverter.toEvent(
        inviteCode,
        request.title(),
        request.description(),
        request.password(),
        request.startAt(),
        request.endAt(),
        request.teamCount()
    );

    event = eventRepository.save(event);

    // HOST MemberEvent 생성
    MemberEvent hostRelation = eventConverter.toHostRelation(member, event);
    memberEventRepository.save(hostRelation);

    // 그룹 생성
    for (int i = 1; i <= request.teamCount(); i++) {
      Group group = eventConverter.toGroup(i, member, event);
      groupRepository.save(group);
    }

    return eventConverter.toCreateResponse(event, member);
  }

  // 🎟️ 이벤트 입장
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

    // 처음 입장 → PARTICIPANT 등록
    if (memberEvent == null) {
      memberEvent = MemberEvent.builder()
          .member(member)
          .event(event)
          .role(MemberEventRole.PARTICIPANT)
          .nickname(request.nickname())
          .build();
      memberEventRepository.save(memberEvent);
    }
    // 기존 입장 기록 있음 → 닉네임만 업데이트
    else {
      memberEvent.updateNickname(request.nickname());
    }

    return eventConverter.toJoinResponse(event, memberEvent);
  }

  // 📌 이벤트 + 그룹 목록 조회
  @Transactional(readOnly = true)
  @Override
  public EventResponse.EventWithGroupsResponse getEventGroups(Member member, Long eventId) {

    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    boolean isParticipant =
        memberEventRepository.existsByMemberAndEventAndIsDeletedFalse(member, event);

    if (!isParticipant) {
      throw new EventHandler(EventErrorStatus.EVENT_ACCESS_DENIED);
    }

    List<Group> groups = groupRepository.findAllByEventId(eventId);

    return eventConverter.toEventWithGroupsResponse(event, groups);
  }

  // 📝 그룹별 게시글 + 투표 조회
  @Transactional(readOnly = true)
  @Override
  public EventResponse.GroupPostsResponse getGroupPosts(Member member, Long eventId, Long groupId) {

    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    boolean isParticipant =
        memberEventRepository.existsByMemberAndEventAndIsDeletedFalse(member, event);

    if (!isParticipant) {
      throw new EventHandler(EventErrorStatus.EVENT_ACCESS_DENIED);
    }

    Group group = groupRepository.findGroupByGroupId(groupId)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.GROUP_NOT_FOUND));

    if (!Objects.equals(group.getEvent().getId(), eventId)) {
      throw new EventHandler(EventErrorStatus.GROUP_NOT_BELONGS_TO_EVENT);
    }

    List<Post> posts = postRepository.findAllByGroupAndIsDeletedFalse(group);

    // 🔥 투표 옵션/퍼센트/댓글/author 모두 포함한 DTO 변환
    return eventConverter.toGroupPostsResponse(group, posts, member);
  }

  // 🔍 초대 코드 유효성 검증
  @Transactional(readOnly = true)
  @Override
  public EventResponse.InviteCodeValidateResponse validateInviteCode(String code) {

    Event event = eventRepository.findByInviteCode(code)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    return new EventResponse.InviteCodeValidateResponse(
        true,
        "초대 코드가 유효합니다.",
        event.getId()
    );
  }

  // 🔐 초대 코드 + 패스워드 검증
  @Transactional(readOnly = true)
  @Override
  public EventResponse.EventPasswordVerifyResponse verifyEventPassword(EventRequest.PasswordVerifyRequest request) {

    Event event = eventRepository.findByInviteCode(request.inviteCode())
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    if (!Objects.equals(event.getPassword(), request.password())) {
      throw new EventHandler(EventErrorStatus.EVENT_PASSWORD_INVALID);
    }

    return EventResponse.EventPasswordVerifyResponse.builder()
        .valid(true)
        .eventId(event.getId())
        .title(event.getTitle())
        .message("비밀번호가 일치합니다.")
        .build();
  }

  public List<MemberListDto.MemberDto> getMembersByEvent(long eventId){
    Event event = eventRepository.findByIdAndIsDeletedFalse(eventId).orElseThrow(
            () -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND)
    );
    List<MemberEvent> me = memberEventRepository.findMemberEventsByEventAndIsDeletedFalse(event);
    List<MemberListDto.MemberDto> members = me.stream()
            .map(m -> MemberListDto.MemberDto.from(m.getMember()))
            .toList();
    return  members;
  }
}
