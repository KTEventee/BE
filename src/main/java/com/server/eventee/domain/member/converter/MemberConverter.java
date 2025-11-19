package com.server.eventee.domain.member.converter;

import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.event.model.MemberEvent;
import com.server.eventee.domain.member.dto.MemberMyPageResponse;
import com.server.eventee.domain.member.model.Member;
import java.util.Objects;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberConverter {

  public MemberMyPageResponse toResponse(Member member, List<Event> joinedEvents) {

    List<MemberMyPageResponse.JoinedEvent> joinedEventDtos = joinedEvents.stream()
        .map(this::toJoinedEvent)
        .toList();

    // 안전 처리된 프로필 이미지 URL
    String profileImageUrl = member.getProfileImageUrl();
    if (profileImageUrl != null && profileImageUrl.isBlank()) {
      profileImageUrl = null;
    }

    return MemberMyPageResponse.builder()
        .nickname(member.getNickname())
        .profileImageUrl(profileImageUrl)
        .joinedEvents(joinedEventDtos)
        .build();
  }


  public MemberMyPageResponse.JoinedEvent toJoinedEvent(Event event) {

    // 참여자 프로필 이미지 상위 3개 가져오기
    List<String> profileImages = event.getMemberEvents().stream()
        .limit(3)
        .map(me -> me.getMember().getProfileImageUrl())
        .filter(Objects::nonNull)
        .toList();

    return MemberMyPageResponse.JoinedEvent.builder()
        .eventId(event.getId())
        .title(event.getTitle())
        .thumbnailUrl(event.getThumbnailUrl())
        .inviteCode(event.getInviteCode())
        .startAt(event.getStartAt())
        .endAt(event.getEndAt())
        .participantsCount(event.getMemberEvents().size())
        .participantProfileImages(profileImages)
        .date(event.getStartAt().toLocalDate()) // 일자만 추출
        .build();
  }

}
