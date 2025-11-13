package com.server.eventee.domain.comment.service;

import com.server.eventee.domain.comment.dto.CommentRequest;
import com.server.eventee.domain.member.model.Member;

public interface CommentService {
    void makeComment(CommentRequest.CommentDto request, Member member);
    void deleteComment(long id);
    void updateComment(CommentRequest.CommentUpdateDto request);
}
