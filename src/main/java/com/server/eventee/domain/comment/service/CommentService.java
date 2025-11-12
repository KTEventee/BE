package com.server.eventee.domain.comment.service;

import com.server.eventee.domain.comment.dto.CommentRequest;

public interface CommentService {
    void makeComment(CommentRequest.CommentDto request);
    void deleteComment(long id);
    void updateComment(CommentRequest.CommentUpdateDto request);
}
