package com.server.eventee.domain.post.dto;

import com.server.eventee.domain.post.model.Post;
import com.server.eventee.domain.post.model.PostType;
import com.server.eventee.domain.comment.dto.CommentResponse;
import com.server.eventee.domain.group.model.Group;

import java.util.ArrayList;
import java.util.List;

public class PostResponse {
    public record PostDto(
            long postId,
            String content,
            String writerName,
            String type,
            String voteTitle,
            String voteContent,
            List<CommentResponse.CommentDto> comments,
            List<VoteLogResponseDto> votedLogs
    ){
        public static PostDto from(Post post){
            List<CommentResponse.CommentDto> comments=new ArrayList<>();
            List<VoteLogResponseDto> votedLogs=new ArrayList<>();

            if(post.getPostType().equals(PostType.TEXT)) comments = CommentResponse.CommentDto.from(post.getComments());
            else if(post.getPostType().equals(PostType.VOTE)) votedLogs = VoteLogResponseDto.from(post.getVoteLogs());
            return new PostDto(
                    post.getPostId(),
                    post.getContent(),
                    null,
                    post.getPostType().type,
                    post.getVoteTitle(),
                    post.getVoteContent(),
                    comments,
                    votedLogs
            );
        }

        public List<PostDto> from(List<Post> posts){
            return posts.stream()
                    .map(PostDto::from)
                    .toList();
        }
    }

    public record PostListDto(
            int groupNum,
            List<PostDto> posts
    ){
        public static PostListDto from(Group g){
            return new PostListDto(
                    g.getGroupNo(),
                    g.getPosts().stream().map(
                            PostDto::from
                    ).toList()
            );
        }
    }

    public record PostListByGroupDto(
            List<PostListDto> lists
    ){
        public static PostListByGroupDto from(List<Group> groups){
            List<PostListDto> listDtos = new ArrayList<>();
            for(Group g : groups){
                listDtos.add(PostListDto.from(g));
            }
            return new PostListByGroupDto(listDtos);
        }
    }
}