package com.server.eventee.domain.comment.service;

import com.server.eventee.domain.Post.model.Post;
import com.server.eventee.domain.Post.repository.PostRepository;
import com.server.eventee.domain.comment.dto.CommentRequest;
import com.server.eventee.domain.comment.model.Comment;
import com.server.eventee.domain.comment.repository.CommentRepository;
import com.server.eventee.domain.group.model.Group;
import com.server.eventee.domain.group.repository.GroupRepository;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;


    @Transactional
    public void makeComment(CommentRequest.CommentDto request,Member member){

        Post post = loadPostById(request.postId());

        Comment comment = Comment.builder()
                .content(request.contnet())
                .post(post)
                .member(member)
                .build();

        post.addComment(commentRepository.save(comment));
        postRepository.save(post);
    }

    @Transactional
    public void deleteComment(long id){
        Comment comment = loadCommentById(id);

        Post post = comment.getPost();
        post.deleteComment(comment);

        commentRepository.delete(comment);
        postRepository.save(post);
    }

    @Transactional
    public void updateComment(CommentRequest.CommentUpdateDto request){
        Comment comment = loadCommentById(request.id());
        comment.updateComment(request);
        commentRepository.save(comment);
    }

    private Comment loadCommentById(long id){
        return commentRepository.findCommentByCommentId(id).orElseThrow(
                () -> new BaseException(ErrorCode.COMMENT_NOT_FOUND)
        );
    }

    private Post loadPostById(long id){
        return postRepository.findPostByPostId(id).orElseThrow(
                () -> new BaseException(ErrorCode.POST_NOT_FOUND)
        );
    }
}
