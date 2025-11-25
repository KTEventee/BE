package com.server.eventee.domain.event.service;

import com.server.eventee.domain.event.dto.EventRequest;
import com.server.eventee.domain.event.dto.EventResponse;
import com.server.eventee.domain.event.dto.MemberListDto;
import com.server.eventee.domain.member.model.Member;

import java.util.List;

public interface EventService {

  EventResponse.CreateResponse createEvent(Member member, EventRequest.CreateRequest request);

  EventResponse.JoinResponse joinEvent(Member member, EventRequest.JoinRequest inviteCode);
  EventResponse.EventWithGroupsResponse getEventGroups(Member member, Long eventId);
  EventResponse.GroupPostsResponse getGroupPosts(Member member, Long eventId, Long groupId);

  EventResponse.InviteCodeValidateResponse validateInviteCode(String code);

  EventResponse.EventPasswordVerifyResponse verifyEventPassword(EventRequest.PasswordVerifyRequest request);

  List<MemberListDto.MemberDto> getMembersByEvent(long eventId, Member member);
  void kickMember(EventRequest.KickMemberRequest request, Member member);
}

