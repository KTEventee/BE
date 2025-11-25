package com.server.eventee.domain.post.service;

import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.post.dto.PostRequest;
import com.server.eventee.domain.post.dto.PostRequest.PostDto;
import com.server.eventee.domain.post.dto.PostResponse;
import com.server.eventee.domain.post.dto.VoteLogResponseDto;
import com.server.eventee.domain.post.model.Post;

public interface PostService {

    Post makePost(PostDto request, Member member);
    void deletePost(long id);

    PostResponse.PostDto
    updatePost(PostRequest.PostDto request,Member member,Long postId);
    PostResponse.PostListByGroupDto getPostByEvent(long eventId,Member member);
    VoteLogResponseDto vote(PostRequest.VoteDto request, Member member);

    void adminPost(PostRequest.AdminPostDto request,Member member);
}
