package com.server.eventee.domain.post.dto;

public class PostRequest {

    public record PostDto(
            Long groupId,
            String type,
            String content
    ){}

    public record DeleteDto(
            Long groupId
    ){}

    public record VoteDto(
            Long postId,
            String voteText
    ){}
}
