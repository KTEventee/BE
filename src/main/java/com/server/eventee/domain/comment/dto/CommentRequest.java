package com.server.eventee.domain.comment.dto;

public class CommentRequest {
    public record CommentDto(
            String contnet,
            Long postId
    ){}

    public record CommentUpdateDto(
            long id,
            String contnet
    ){}

}
