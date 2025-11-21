package com.server.eventee.domain.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Schema(description = "이벤트 응답 DTO 모음")
public class EventResponse {

  // ======================================================
  // 🎉 1. 이벤트 생성 응답
  // ======================================================
  @Schema(description = "이벤트 생성 응답 DTO")
  @Builder
  public record CreateResponse(
      @Schema(description = "이벤트 ID") Long eventId,
      @Schema(description = "이벤트 제목") String title,
      @Schema(description = "자동 생성된 초대 코드 (대문자 6자리)") String inviteCode,
      @Schema(description = "이벤트 초대 URL") String inviteUrl,
      @Schema(description = "이벤트 시작 시각") LocalDateTime startAt,
      @Schema(description = "이벤트 종료 시각") LocalDateTime endAt,
      @Schema(description = "이벤트 생성 시각") LocalDateTime createdAt,
      @Schema(description = "이벤트 생성자 정보") CreatorInfo creator
  ) {

    @Builder
    @Schema(description = "이벤트 생성자 정보 DTO")
    public record CreatorInfo(
        @Schema(description = "회원 ID") Long memberId,
        @Schema(description = "회원 닉네임") String nickname,
        @Schema(description = "프로필 이미지 URL") String profileImageUrl
    ) {}
  }

  // ======================================================
  // 🎟️ 2. 초대 코드 입장 응답
  // ======================================================
  @Schema(description = "이벤트 초대 코드 입장 응답 DTO")
  @Builder
  public record JoinResponse(
      Long eventId,
      String title,
      String description,
      String thumbnailUrl,
      Integer teamCount,
      String role,
      String nickname,
      List<GroupInfo> groups
  ) {

    @Builder
    @Schema(description = "이벤트 그룹 정보 DTO")
    public record GroupInfo(
        Long groupId,
        String groupName,
        String groupDescription,
        String groupImg,
        int groupNo,
        String groupLeader
    ) {}
  }

  // ======================================================
  // 🧭 3. 이벤트 + 그룹 목록 응답
  // ======================================================
  @Schema(description = "이벤트 + 그룹 목록 응답 DTO")
  @Builder
  public record EventWithGroupsResponse(
      Long eventId,
      String eventTitle,
      String eventDescription,
      String thumbnailUrl,
      LocalDateTime startAt,
      LocalDateTime endAt,
      Integer teamCount,
      List<GroupSummary> groups
  ) {

    @Builder
    @Schema(description = "이벤트 내 그룹 요약 DTO")
    public record GroupSummary(
        Long groupId,
        String groupName,
        String groupDescription,
        String groupImg,
        int groupNo,
        String groupLeader
    ) {}
  }

  // ======================================================
  // 🗳️ 4. 그룹별 포스트 / 투표 응답
  // ======================================================
  @Schema(description = "그룹별 포스트 및 투표 조회 응답 DTO")
  @Builder
  public record GroupPostsResponse(
      Long groupId,
      String groupName,
      List<PostInfo> posts
  ) {

    // ------------------------
    // 📝 포스트 정보
    // ------------------------
    @Builder
    @Schema(description = "포스트 정보 DTO")
    public record PostInfo(
        Long postId,
        String author,
        String content,
        String type,
        LocalDateTime createdAt,

        // 댓글
        List<CommentInfo> comments,

        // 투표 (voteType일 때만)
        String pollQuestion,
        List<VoteOptionInfo> pollOptions,
        Integer userVote
    ) {}

    // ------------------------
    // 🗳️ 투표 옵션 정보
    // ------------------------
    @Builder
    @Schema(description = "투표 옵션 정보 DTO")
    public record VoteOptionInfo(
        @Schema(description = "옵션 번호") int optionNo,
        @Schema(description = "옵션 텍스트") String text,
        @Schema(description = "득표 수") int votes,
        @Schema(description = "득표율 (%)") int percent,
        @Schema(description = "본인이 선택한 옵션 여부") boolean isMine
    ) {}

    // ------------------------
    // 💬 댓글 정보
    // ------------------------
    @Builder
    @Schema(description = "댓글 정보 DTO")
    public record CommentInfo(
        Long commentId,
        String content,
        String writerNickname,
        String writerProfileUrl,
        LocalDateTime createdAt
    ) {}
  }

  // ======================================================
  // ✔ 5. 초대 코드 유효성 검증
  // ======================================================
  @Schema(description = "초대 코드 유효성 검증 응답 DTO")
  @Builder
  public record InviteCodeValidateResponse(
      boolean valid,
      String message,
      Long eventId
  ) {}

  // ======================================================
  // 🔐 6. 초대 코드 + 비밀번호 검증
  // ======================================================
  @Schema(description = "초대 코드 + 비밀번호 검증 응답 DTO")
  @Builder
  public record EventPasswordVerifyResponse(
      boolean valid,
      Long eventId,
      String title,
      String message
  ) {}

}
