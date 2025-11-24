package com.server.eventee.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class PostRequest {

    @Schema(description = "게시글 생성 요청 DTO")
    public record PostDto(

            @Schema(description = "그룹 ID", example = "4")
            Long groupId,

            @Schema(description = "게시글 타입(TEXT 또는 VOTE)", example = "TEXT")
            String type,

            @Schema(description = "게시글 본문 내용", example = "오늘 저녁 7시에 회의 있습니다!")
            String content,

            @Schema(description = "투표 제목(VOTE 타입일 때만 사용)", example = "저녁 메뉴 투표")
            String voteTitle,

            @Schema(description = "투표 항목들( '_'로 구분)", example = "치킨, 피자, 햄버거")
            String voteContent
    ) {}

    @Schema(description = "게시글 삭제 요청 DTO")
    public record DeleteDto(

            @Schema(description = "그룹 ID", example = "4")
            Long groupId
    ) {}

    @Schema(description = "투표 요청 DTO")
    public record VoteDto(

            @Schema(description = "게시글 ID", example = "17")
            Long postId,

            @Schema(description = "투표 선택지 텍스트", example = "치킨")
            String voteText
    ) {}

    public record AdminPostDto(
            @Schema(description = "게시글 타입(TEXT 또는 VOTE)", example = "TEXT")
            String type,

            @Schema(description = "게시글 본문 내용", example = "오늘 저녁 7시에 회의 있습니다!")
            String content,

            @Schema(description = "투표 제목(VOTE 타입일 때만 사용)", example = "저녁 메뉴 투표")
            String voteTitle,

            @Schema(description = "투표 항목들( '_'로 구분)", example = "치킨, 피자, 햄버거")
            String voteContent,

            @Schema(description = "투표 항목들( '_'로 구분)", example = "1_2_3_4")
            String groupNums
    ){}
}
