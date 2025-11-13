package com.server.eventee.domain.Post.dto;

public class PostRequest {

    public record PostDto(
            Long groupId,
            String type,
            String content,
            String voteTitle,
            String voteContent
    ){}

    public record DeleteDto(
            Long groupId
    ){}

    public record VoteDto(
            Long postId,
            String voteText
    ){}
}
