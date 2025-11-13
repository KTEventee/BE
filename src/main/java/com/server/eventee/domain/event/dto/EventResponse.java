package com.server.eventee.domain.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
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
      String role,

      @Schema(description = "이벤트 내 그룹(팀) 목록")
      List<GroupInfo> groups
  ) {

    @Builder
    @Schema(description = "그룹(팀) 정보 DTO")
    public record GroupInfo(
        @Schema(description = "그룹 ID", example = "1")
        Long groupId,

        @Schema(description = "그룹 이름", example = "1조")
        String groupName,

        @Schema(description = "그룹 설명", example = "자동 생성된 그룹입니다.")
        String groupDescription,

        @Schema(description = "그룹 이미지", example = "https://eventee-bucket.s3.ap-northeast-2.amazonaws.com/group/defaultGroupImage.png")
        String groupImg,

        @Schema(description = "그룹 순번", example = "1")
        int groupNo,

        @Schema(description = "그룹 리더 닉네임", example = "혜진님")
        String groupLeader
    ) {}
  }
  @Schema(description = "이벤트 + 그룹 목록 응답 DTO")
  @Builder
  public record EventWithGroupsResponse(

      @Schema(description = "이벤트 ID", example = "1")
      Long eventId,

      @Schema(description = "이벤트 제목", example = "xx대학교 MT")
      String eventTitle,

      @Schema(description = "이벤트 설명", example = "즐거운 1박 2일 MT 일정입니다.")
      String eventDescription,

      @Schema(description = "이벤트 썸네일 이미지 URL")
      String thumbnailUrl,

      @Schema(description = "이벤트 시작 시각")
      LocalDateTime startAt,

      @Schema(description = "이벤트 종료 시각")
      LocalDateTime endAt,

      @Schema(description = "팀(조) 개수", example = "4")
      Integer teamCount,

      @Schema(description = "이벤트 내 그룹 목록")
      List<GroupSummary> groups
  ) {

    @Builder
    @Schema(description = "이벤트 내 그룹 요약 DTO")
    public record GroupSummary(
        @Schema(description = "그룹 ID", example = "10")
        Long groupId,

        @Schema(description = "그룹 이름", example = "1조")
        String groupName,

        @Schema(description = "그룹 설명", example = "자동 생성된 그룹입니다.")
        String groupDescription,

        @Schema(description = "그룹 이미지", example = "https://eventee-bucket.s3.ap-northeast-2.amazonaws.com/group/defaultGroupImage.png")
        String groupImg,

        @Schema(description = "그룹 순번", example = "1")
        int groupNo,

        @Schema(description = "그룹 리더 닉네임", example = "혜진님")
        String groupLeader
    ) {}
  }
  @Schema(description = "그룹별 포스트 및 투표 조회 응답 DTO")
  @Builder
  public record GroupPostsResponse(
      @Schema(description = "그룹 ID", example = "10")
      Long groupId,

      @Schema(description = "그룹 이름", example = "1조")
      String groupName,

      @Schema(description = "포스트 목록")
      List<PostInfo> posts
  ) {

    @Builder
    @Schema(description = "포스트 정보 DTO")
    public record PostInfo(
        @Schema(description = "포스트 ID", example = "100")
        Long postId,

        @Schema(description = "포스트 내용", example = "오늘 점심 뭐 먹을까요?")
        String content,

        @Schema(description = "포스트 타입(TEXT / VOTE)", example = "TEXT")
        String postType,

        @Schema(description = "생성 시각", example = "2025-11-12T09:00:00")
        LocalDateTime createdAt,

        @Schema(description = "댓글 목록")
        List<CommentInfo> comments,

        @Schema(description = "투표 목록")
        List<VoteLogInfo> voteLogs
    ) {}

    @Builder
    @Schema(description = "댓글 정보 DTO")
    public record CommentInfo(
        @Schema(description = "댓글 ID", example = "1")
        Long commentId,

        @Schema(description = "댓글 내용", example = "김치찌개요!")
        String content,

        @Schema(description = "작성자 닉네임", example = "혜진님")
        String writerNickname,

        @Schema(description = "작성자 프로필 이미지", example = "https://eventee-bucket.s3.ap-northeast-2.amazonaws.com/profile/7.jpg")
        String writerProfileUrl,

        @Schema(description = "작성 시각", example = "2025-11-12T09:05:00")
        LocalDateTime createdAt
    ) {}

    @Builder
    @Schema(description = "투표 정보 DTO")
    public record VoteLogInfo(
        @Schema(description = "투표 ID", example = "3")
        Long voteLogId,

        @Schema(description = "투표 항목", example = "치킨")
        String voteWord
    ) {}

  }

}
