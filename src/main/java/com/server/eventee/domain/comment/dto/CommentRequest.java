package com.server.eventee.domain.comment.dto;

public class CommentRequest {
    public record CommentDto(
            String content,
            Long postId
    ){}

    public record CommentUpdateDto(
            long id,
            String content
    ){}

}
