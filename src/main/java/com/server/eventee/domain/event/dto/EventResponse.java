package com.server.eventee.domain.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Schema(description = "이벤트 응답 DTO 모음")
public class EventResponse {

  // 1. 이벤트 생성 응답
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

  // 2. 초대 코드 입장 응답
  @Schema(description = "이벤트 초대 코드 입장 응답 DTO")
  @Builder
  public record JoinResponse(
      @Schema(description = "이벤트 ID") Long eventId,
      @Schema(description = "이벤트 제목") String title,
      @Schema(description = "이벤트 설명") String description,
      @Schema(description = "썸네일 URL") String thumbnailUrl,
      @Schema(description = "팀 개수") Integer teamCount,
      @Schema(description = "이벤트 내 역할 (HOST, PARTICIPANT)") String role,
      @Schema(description = "이벤트 내 닉네임") String nickname,
      @Schema(description = "그룹 정보 리스트") List<GroupInfo> groups
  ) {

    @Builder
    @Schema(description = "이벤트 그룹 정보 DTO")
    public record GroupInfo(
        @Schema(description = "그룹 ID") Long groupId,
        @Schema(description = "그룹 이름") String groupName,
        @Schema(description = "그룹 설명") String groupDescription,
        @Schema(description = "그룹 이미지 URL") String groupImg,
        @Schema(description = "그룹 번호") int groupNo,
        @Schema(description = "그룹장 닉네임") String groupLeader
    ) {}
  }

  // 3. 이벤트 + 그룹 목록 응답
  @Schema(description = "이벤트 + 그룹 목록 응답 DTO")
  @Builder
  public record EventWithGroupsResponse(
      @Schema(description = "이벤트 ID") Long eventId,
      @Schema(description = "이벤트 제목") String eventTitle,
      @Schema(description = "이벤트 설명") String eventDescription,
      @Schema(description = "썸네일 URL") String thumbnailUrl,
      @Schema(description = "시작일") LocalDateTime startAt,
      @Schema(description = "종료일") LocalDateTime endAt,
      @Schema(description = "팀 개수") Integer teamCount,
      @Schema(description = "그룹 리스트") List<GroupSummary> groups
  ) {

    @Builder
    @Schema(description = "그룹 요약 DTO")
    public record GroupSummary(
        @Schema(description = "그룹 ID") Long groupId,
        @Schema(description = "그룹 이름") String groupName,
        @Schema(description = "그룹 설명") String groupDescription,
        @Schema(description = "그룹 이미지 URL") String groupImg,
        @Schema(description = "그룹 번호") int groupNo,
        @Schema(description = "그룹장 닉네임") String groupLeader
    ) {}
  }

  // 🗳️ 4. 그룹별 포스트 / 투표 응답
  @Schema(description = "그룹별 포스트 및 투표 조회 응답 DTO")
  @Builder
  public record GroupPostsResponse(
      @Schema(description = "그룹 ID") Long groupId,
      @Schema(description = "그룹 이름") String groupName,
      @Schema(description = "게시글 리스트") List<PostInfo> posts
  ) {

    // 📝 포스트 정보
    @Builder
    @Schema(description = "포스트 정보 DTO")
    public record PostInfo(
        @Schema(description = "게시글 ID") Long postId,
        @Schema(description = "작성자 닉네임") String author,
        @Schema(description = "내용") String content,
        @Schema(description = "포스트 타입 (text / vote)") String type,
        @Schema(description = "작성일") LocalDateTime createdAt,

        @Schema(description = "댓글 리스트") List<CommentInfo> comments,

        // 투표
        @Schema(description = "투표 질문 (vote일 때만)") String pollQuestion,
        @Schema(description = "투표 옵션 리스트") List<VoteOptionInfo> pollOptions,
        @Schema(description = "내가 선택한 옵션 번호 (없으면 null)") Integer userVote,

        @Schema(description = "내가 작성한 글인지 여부") boolean isMine
    ) {}

    // 투표 옵션 정보
    @Builder
    @Schema(description = "투표 옵션 정보 DTO")
    public record VoteOptionInfo(
        @Schema(description = "옵션 번호") int optionNo,
        @Schema(description = "옵션 텍스트") String text,
        @Schema(description = "득표 수") int votes,
        @Schema(description = "득표율 (%)") int percent,
        @Schema(description = "본인이 선택한 옵션 여부") boolean isMine
    ) {}

    // 댓글 정보
    @Builder
    @Schema(description = "댓글 정보 DTO")
    public record CommentInfo(
        @Schema(description = "댓글 ID") Long commentId,
        @Schema(description = "내용") String content,
        @Schema(description = "작성자 닉네임") String writerNickname,
        @Schema(description = "작성자 프로필 URL") String writerProfileUrl,
        @Schema(description = "작성일") LocalDateTime createdAt,
        @Schema(description = "내가 작성한 댓글인지 여부") boolean isMine
    ) {}
  }

  // 5. 초대 코드 유효성 검증
  @Schema(description = "초대 코드 유효성 검증 응답 DTO")
  @Builder
  public record InviteCodeValidateResponse(
      @Schema(description = "유효 여부") boolean valid,
      @Schema(description = "결과 메시지") String message,
      @Schema(description = "이벤트 ID") Long eventId
  ) {}

  // 6. 초대 코드 + 비밀번호 검증
  @Schema(description = "초대 코드 + 비밀번호 검증 응답 DTO")
  @Builder
  public record EventPasswordVerifyResponse(
      @Schema(description = "비밀번호 일치 여부") boolean valid,
      @Schema(description = "이벤트 ID") Long eventId,
      @Schema(description = "이벤트 제목") String title,
      @Schema(description = "결과 메시지") String message
  ) {}
}
