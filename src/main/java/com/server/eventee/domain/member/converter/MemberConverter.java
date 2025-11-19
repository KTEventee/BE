package com.server.eventee.domain.member.converter;

import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.event.model.MemberEvent;
import com.server.eventee.domain.member.dto.MemberMyPageResponse;
import com.server.eventee.domain.member.model.Member;
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


  private MemberMyPageResponse.JoinedEvent toJoinedEvent(Event event) {
    List<String> participantImages = event.getMemberEvents().stream()
        .map(MemberEvent::getMember)
        .map(Member::getProfileImageUrl)
        .filter(url -> url != null && !url.isBlank())
        .limit(3)
        .toList();

    return MemberMyPageResponse.JoinedEvent.builder()
        .eventId(event.getId())
        .title(event.getTitle())
        .thumbnailUrl(event.getThumbnailUrl())
        .participantsCount(event.getMemberEvents().size())
        .participantProfileImages(participantImages)
        .date(event.getCreatedAt().toLocalDate())
        .build();
  }
}
