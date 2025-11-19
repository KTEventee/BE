package com.server.eventee.domain.post.service;

import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.post.dto.PostRequest;
import com.server.eventee.domain.post.dto.PostResponse;

public interface PostService {

    void makePost(PostRequest.PostDto request, Member member);
    void deletePost(long id);
    PostResponse.PostDto updatePost(PostRequest.PostDto request,Member member);
    PostResponse.PostListByGroupDto getPostByEvent(long eventId,Member member);
    void vote(PostRequest.VoteDto request,Member member);
}
