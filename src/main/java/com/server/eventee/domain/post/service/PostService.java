package com.server.eventee.domain.post.service;

import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.post.dto.PostRequest;
import com.server.eventee.domain.post.dto.PostResponse;
import com.server.eventee.domain.post.dto.VoteLogResponseDto;

public interface PostService {

    void makePost(PostRequest.PostDto request, Member member);
    void deletePost(long id);
    PostResponse.PostDto updatePost(PostRequest.PostDto request,Member member);
    PostResponse.PostListByGroupDto getPostByEvent(long eventId,Member member);
    VoteLogResponseDto vote(PostRequest.VoteDto request, Member member);
}
