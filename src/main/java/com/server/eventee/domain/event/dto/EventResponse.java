package com.server.eventee.domain.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.time.LocalDateTime;

@Schema(description = "이벤트 응답 DTO 모음")
public class EventResponse {

  @Schema(description = "이벤트 생성 응답 DTO")
  @Builder
  public record CreateResponse(
      @Schema(description = "이벤트 ID", example = "15")
      Long eventId,

      @Schema(description = "이벤트 제목", example = "봄 소풍 MT")
      String title,

      @Schema(description = "자동 생성된 초대 코드 (대문자 6자리)", example = "ABCDEF")
      String inviteCode,

      @Schema(description = "이벤트 초대 URL", example = "https://eventee.site/invite/ABCDEF")
      String inviteUrl,

      @Schema(description = "이벤트 시작 시각", example = "2025-04-10T09:00:00")
      LocalDateTime startAt,

      @Schema(description = "이벤트 종료 시각", example = "2025-04-10T18:00:00")
      LocalDateTime endAt,

      @Schema(description = "이벤트 생성 시각", example = "2025-03-01T11:32:45")
      LocalDateTime createdAt,

      @Schema(description = "이벤트 생성자 정보")
      CreatorInfo creator
  ) {
    @Builder
    @Schema(description = "이벤트 생성자 정보 DTO")
    public record CreatorInfo(
        @Schema(description = "회원 ID", example = "7")
        Long memberId,

        @Schema(description = "회원 닉네임", example = "혜진님")
        String nickname,

        @Schema(description = "회원 프로필 이미지 URL", example = "https://eventee-bucket.s3.ap-northeast-2.amazonaws.com/profile/7.jpg")
        String profileImageUrl
    ) {}
  }

  @Schema(description = "이벤트 초대 코드 입장 응답 DTO")
  @Builder
  public record JoinResponse(
      @Schema(description = "이벤트 ID", example = "15")
      Long eventId,

      @Schema(description = "이벤트 제목", example = "봄 소풍 MT")
      String title,

      @Schema(description = "이벤트 설명", example = "교내 동아리 봄 소풍 행사입니다.")
      String description,

      @Schema(description = "이벤트 썸네일 이미지", example = "https://eventee-bucket.s3.ap-northeast-2.amazonaws.com/event/defaultEventImage.png")
      String thumbnailUrl,

      @Schema(description = "이벤트의 조(팀) 개수", example = "3")
      Integer teamCount,

      @Schema(description = "현재 사용자의 이벤트 내 역할 (HOST 또는 PARTICIPANT)", example = "PARTICIPANT")
      String role
  ) {}
}
