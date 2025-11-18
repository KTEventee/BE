package com.server.eventee.domain.post.service;

import com.server.eventee.domain.post.dto.PostRequest;
import com.server.eventee.domain.post.dto.PostResponse;
import com.server.eventee.domain.post.model.Post;
import com.server.eventee.domain.post.model.PostType;
import com.server.eventee.domain.post.model.VoteLog;
import com.server.eventee.domain.post.repository.PostRepository;
import com.server.eventee.domain.post.repository.VoteLogRepository;
import com.server.eventee.domain.group.model.Group;
import com.server.eventee.domain.group.repository.GroupRepository;
import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
@Slf4j
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final GroupRepository groupRepository;
    private final VoteLogRepository voteLogRepository;

    /*
        fixme 전체적인 수정사항
            그룹이랑 연결해줘야함.
     */

    @Transactional
    public void makePost(PostRequest.PostDto request){
        Group group = groupRepository.findGroupByGroupId(request.groupId())
                .orElseThrow(() -> new BaseException(ErrorCode.GROUP_NOT_FOUND));

        PostType postType = PostType.from(request.type());

        Post post = Post.builder()
                .content(request.content())
                .type(postType)
                .group(group)
                .voteTitle(request.voteTitle())
                .voteContent(request.voteContent())
                .build();

        group.addPost(post);
        groupRepository.save(group);
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(long id){
        Post post = loadPostById(id);
        Group group = post.getGroup();

        group.deletePost(post);
        postRepository.delete(post);
        groupRepository.save(group);
    }

    @Transactional
    public void updatePost(PostRequest.PostDto request){
        Post post = loadPostById(request.groupId());
        post.updatePost(request);
        postRepository.save(post);
    }

    private Post loadPostById(long id){
        return postRepository.findPostByPostId(id).orElseThrow(
                () -> new BaseException(ErrorCode.POST_NOT_FOUND)
        );
    }

    public PostResponse.PostListByGroupDto getPostByEvent(long evnetId){
        List<Group> groups = new ArrayList<>();
        return PostResponse.PostListByGroupDto.from(groups);
    }

    public void vote(PostRequest.VoteDto request){
        Post post = loadPostById(request.postId());

        if(!post.getPostType().equals(PostType.VOTE)){
            throw new BaseException(
                    ErrorCode.POST_TYPE_NOT_VOTE
            );
        }

        VoteLog log = VoteLog.builder()
                .post(post)
                .word(request.voteText())
                .build();

        post.addVoteLog(log);

        voteLogRepository.save(log);
        postRepository.save(post);
    }


}
