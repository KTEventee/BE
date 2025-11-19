package com.server.eventee.domain.comment.dto;

import com.server.eventee.domain.comment.model.Comment;
import com.server.eventee.domain.member.model.Member;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {
    public record CommentDto(
            long commentId,
            String writer,
            String content,
            boolean isWrite,
            LocalDateTime createdAt
    ){
        public static CommentDto from(Comment c, Member member){
            String writer = c.getMember().getNickname();
            return new CommentDto(
                    c.getCommentId(),
                    writer,
                    c.getContent(),
                    writer.equals(member.getNickname()),
                    c.getCreatedAt()
            );
        }

        public static List<CommentDto> from(List<Comment> comments,Member member){
            return comments.stream().map(
                    comment -> from(comment,member)
            ).toList();
        }
    }
}
