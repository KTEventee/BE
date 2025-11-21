package com.server.eventee.domain.member.converter;

import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.event.model.MemberEvent;
import com.server.eventee.domain.member.dto.MemberMyPageResponse;
import com.server.eventee.domain.member.model.Member;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class MemberConverter {

  public MemberMyPageResponse toResponse(Member member, List<Event> joinedEvents) {

    List<MemberMyPageResponse.JoinedEvent> joinedEventDtos = joinedEvents.stream()
        .map(event -> toJoinedEvent(event, member)) // ✅ member 정보도 함께 넘김
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

  public MemberMyPageResponse.JoinedEvent toJoinedEvent(Event event, Member member) {

    MemberEvent myMemberEvent = event.getMemberEvents().stream()
        .filter(me -> me.getMember() != null
            && me.getMember().getId().equals(member.getId()))
        .findFirst()
        .orElse(null);

    String role = null;
    if (myMemberEvent != null && myMemberEvent.getRole() != null) {
      // (1) role이 Enum인 경우
      role = myMemberEvent.getRole().name();

      // (2) role 필드가 String이면 아래처럼:
      // role = myMemberEvent.getRole();
    }

    // 참여자 프로필 이미지 상위 3개 가져오기
    List<String> profileImages = event.getMemberEvents().stream()
        .map(me -> me.getMember().getProfileImageUrl())
        .filter(Objects::nonNull)
        .limit(3)
        .toList();

    return MemberMyPageResponse.JoinedEvent.builder()
        .eventId(event.getId())
        .title(event.getTitle())
        .thumbnailUrl(event.getThumbnailUrl())
        .inviteCode(event.getInviteCode())
        .role(role)
        .startAt(event.getStartAt())
        .endAt(event.getEndAt())
        .participantsCount(event.getMemberEvents().size())
        .participantProfileImages(profileImages)
        .date(event.getStartAt().toLocalDate()) // 일자만 추출
        .build();
  }

}
