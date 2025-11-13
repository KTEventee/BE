package com.server.eventee.domain.Post.service;

import com.server.eventee.domain.Post.dto.PostRequest;
import com.server.eventee.domain.Post.dto.PostResponse;

public interface PostService {

    void makePost(PostRequest.PostDto request);
    void deletePost(long id);
    void updatePost(PostRequest.PostDto request);
    PostResponse.PostListByGroupDto getPostByEvent(long evnetId);
    void vote(PostRequest.VoteDto request);
}
