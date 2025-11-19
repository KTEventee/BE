package com.server.eventee.domain.post.dto;

import com.server.eventee.domain.member.model.Member;
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
            VoteLogResponseDto votedLogs,
            boolean isWrite
    ){
        public static PostDto from(Post post, Member member){
            List<CommentResponse.CommentDto> comments=new ArrayList<>();
            VoteLogResponseDto votedLogs= null;
            String writer = post.getMember().getNickname();

            if(post.getPostType().equals(PostType.TEXT)) comments = CommentResponse.CommentDto.from(post.getComments(),member);
            else if(post.getPostType().equals(PostType.VOTE)) votedLogs = VoteLogResponseDto.from(post.getVoteLogs(),member);

            return new PostDto(
                    post.getPostId(),
                    post.getContent(),
                    writer,
                    post.getPostType().type.toLowerCase(),
                    post.getVoteTitle(),
                    post.getVoteContent(),
                    comments,
                    votedLogs,
                    member.getNickname().equals(writer)
            );
        }

        public static List<PostDto> from(List<Post> posts, Member member) {
            return posts.stream()
                    .map(post -> PostDto.from(post, member)) // 🔥 member 전달
                    .toList();
        }
    }

    public record PostListDto(
            int groupNum,
            List<PostDto> posts
    ){
        public static PostListDto from(Group g,Member member){
            return new PostListDto(
                    g.getGroupNo(),
                    g.getPosts().stream().map(post -> PostDto.from(post, member)).toList()
            );
        }
    }

    public record PostListByGroupDto(
            List<PostListDto> lists
    ){
        public static PostListByGroupDto from(List<Group> groups,Member member){
            List<PostListDto> listDtos = new ArrayList<>();
            for(Group g : groups){
                listDtos.add(PostListDto.from(g,member));
            }
            return new PostListByGroupDto(listDtos);
        }
    }
}