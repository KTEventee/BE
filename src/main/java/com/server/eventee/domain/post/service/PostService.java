package com.server.eventee.domain.post.service;

import com.server.eventee.domain.post.dto.PostRequest;
import com.server.eventee.domain.post.dto.PostResponse;

public interface PostService {

    void makePost(PostRequest.PostDto request);
    void deletePost(long id);
    void updatePost(PostRequest.PostDto request);
    PostResponse.PostListByGroupDto getPostByEvent(long evnetId);
    void vote(PostRequest.VoteDto request);
}
