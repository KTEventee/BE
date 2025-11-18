package com.server.eventee.domain.comment.dto;

import com.server.eventee.domain.comment.model.Comment;

import java.util.List;

public class CommentResponse {
    public record CommentDto(
            long commentId,
            String writer,
            String content
    ){
        public static CommentDto from(Comment c){
            //fixme memeber수정
            return new CommentDto(
                    c.getCommentId(),
                    "commentTest",
                    c.getContent()
            );
        }

        public static List<CommentDto> from(List<Comment> comments){
            return comments.stream().map(CommentDto::from).toList();
        }
    }
}
