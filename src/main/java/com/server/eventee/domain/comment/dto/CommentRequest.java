package com.server.eventee.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class CommentRequest {

    public record CommentDto(
            @Schema(description = "댓글 내용", example = "안녕하세요")
            String content,
            @Schema(description = "댓글 단 게시글 ID", example = "7")
            Long postId
    ){}

    public record CommentUpdateDto(
            @Schema(description = "댓글 ID", example = "7")
            long id,
            @Schema(description = "댓글 내용", example = "안녕하세요")
            String content
    ){}

}
