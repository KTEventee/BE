package com.server.eventee.domain.member.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "마이페이지 응답 DTO")
public class MemberMyPageResponse {

  @Schema(description = "회원 닉네임", example = "가나다")
  private String nickname;

  @Schema(description = "회원 프로필 이미지 URL")
  private String profileImageUrl;

  @ArraySchema(
      arraySchema = @Schema(description = "참여한 이벤트 목록"),
      schema = @Schema(implementation = JoinedEvent.class)
  )
  private List<JoinedEvent> joinedEvents;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "참여 이벤트 정보 DTO")
  public static class JoinedEvent {

    @Schema(description = "이벤트 ID", example = "1")
    private Long eventId;

    @Schema(description = "이벤트 제목")
    private String title;

    @Schema(description = "이벤트 썸네일 URL")
    private String thumbnailUrl;

    @Schema(description = "이벤트 초대 코드", example = "ABCD12")
    private String inviteCode;     // ⭐ 추가

    @Schema(description = "이벤트 시작일시", example = "2025-10-25T15:00:00")
    private LocalDateTime startAt; // ⭐ 추가

    @Schema(description = "이벤트 종료일시", example = "2025-10-25T18:00:00")
    private LocalDateTime endAt;   // ⭐ 추가

    @Schema(description = "참여 인원 수")
    private int participantsCount;

    @ArraySchema(
        arraySchema = @Schema(description = "참여자 프로필 이미지"),
        schema = @Schema(example = "https://eventee-bucket/.../profile1.jpg")
    )
    private List<String> participantProfileImages;

    @Schema(description = "이벤트 일자(yyyy-MM-dd)", example = "2025-10-25")
    private LocalDate date;
  }
}
