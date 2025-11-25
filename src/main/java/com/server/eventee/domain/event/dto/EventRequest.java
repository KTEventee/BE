package com.server.eventee.domain.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "이벤트 요청 DTO 모음")
public class EventRequest {

  @Schema(description = "이벤트 생성 요청 DTO")
  public record CreateRequest(

      @NotBlank
      @Schema(description = "이벤트 제목", example = "봄 소풍 MT")
      String title,

      @NotBlank
      @Schema(description = "이벤트 설명", example = "교내 동아리 봄 소풍 행사입니다.")
      String description,

      @NotBlank
      @Schema(description = "이벤트 비밀번호 (입장 시 검증용)", example = "1234")
      String password,

      @NotNull
      @Schema(description = "이벤트 시작 시각", example = "2025-04-10T09:00:00")
      LocalDateTime startAt,

      @NotNull
      @Schema(description = "이벤트 종료 시각", example = "2025-04-10T18:00:00")
      LocalDateTime endAt,

      @NotNull
      @Schema(description = "자동 생성할 조(팀) 개수", example = "3")
      Integer teamCount
  ) {}

  @Schema(description = "이벤트 초대 코드 및 비밀번호 검증 요청 DTO")
  public record JoinRequest(

      @NotBlank
      @Schema(description = "이벤트 초대 코드", example = "ABCDEF")
      String inviteCode,

      @NotBlank
      @Schema(description = "이벤트 비밀번호", example = "1234")
      String password,

      @Schema(description = "이벤트 내에서 사용할 닉네임", example = "가나다")
      @NotBlank String nickname

  ) {}

  @Schema(description = "이벤트 비밀번호 검증 요청 DTO")
  public record PasswordVerifyRequest(

      @NotBlank
      @Schema(description = "이벤트 초대 코드", example = "ABCDEF")
      String inviteCode,

      @NotBlank
      @Schema(description = "이벤트 비밀번호", example = "1234")
      String password
  ) {}

  public record KickMemberRequest(
      long eventId,
      long memberId
  ){}


  @Schema(description = "이벤트 수정 요청 DTO")
  public record UpdateRequest(

      @NotNull
      @Schema(description = "이벤트 ID")
      Long eventId,

      @Schema(description = "수정할 제목")
      String title,

      @Schema(description = "수정할 설명")
      String description,

      @Schema(description = "이벤트 시작일", example = "2025-03-10")
      LocalDate startAt,

      @Schema(description = "이벤트 종료일", example = "2025-03-11")
      LocalDate endAt
  ) {}


}
