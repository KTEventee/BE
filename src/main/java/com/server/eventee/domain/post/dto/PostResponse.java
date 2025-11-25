package com.server.eventee.domain.post.dto;

import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.post.model.Post;
import com.server.eventee.domain.post.model.PostType;
import com.server.eventee.domain.comment.dto.CommentResponse;
import com.server.eventee.domain.group.model.Group;
import com.server.eventee.domain.post.model.VoteLog;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostResponse {

    @Schema(description = "투표 옵션 DTO")
    public record VoteOptionDto(
        @Schema(description = "옵션 번호", example = "1") int optionNo,
        @Schema(description = "옵션 텍스트", example = "짜장면") String text,
        @Schema(description = "득표 수", example = "12") int votes,
        @Schema(description = "득표 비율 (%)", example = "60") int percent,
        @Schema(description = "내가 선택한 옵션 여부", example = "true") boolean isMine
    ) {}

    @Schema(description = "게시글 DTO")
    public record PostDto(
        @Schema(description = "게시글 ID", example = "10") long postId,
        @Schema(description = "내용") String content,
        @Schema(description = "작성자 닉네임") String writerName,
        @Schema(description = "게시글 타입 (text, vote)") String type,
        @Schema(description = "투표 질문") String voteTitle,
        @Schema(description = "투표 옵션 리스트") List<VoteOptionDto> voteOptions,
        @Schema(description = "댓글 리스트") List<CommentResponse.CommentDto> comments,
        @Schema(description = "내가 작성한 글인지 여부") boolean isWrite
    ) {

        public static PostDto from(Post post, Member member) {

            String writer = post.getMember().getNickname();

            List<CommentResponse.CommentDto> comments =
                CommentResponse.CommentDto.from(post.getComments(), member);

            List<VoteOptionDto> voteOptionDtos = new ArrayList<>();

            if (post.getPostType() == PostType.VOTE) {

                // 안정적인 옵션 파싱 (공백/쉼표 여러 형태 모두 처리)
                String[] options = post.getVoteContent() != null
                    ? post.getVoteContent().split("\\s*,\\s*")
                    : new String[0];

                List<VoteLog> logs = post.getVoteLogs();
                int totalVotes = logs.size();

                for (int i = 0; i < options.length; i++) {
                    int optionNo = i + 1;
                    String text = options[i];

                    int votes = (int) logs.stream()
                        .filter(v -> v.getVoteNum() == optionNo)
                        .count();

                    int percent = totalVotes > 0
                        ? (int) Math.round((votes * 100.0) / totalVotes)
                        : 0;

                    boolean isMine = logs.stream()
                        .anyMatch(v ->
                            v.getMember().getId().equals(member.getId()) &&
                                v.getVoteNum() == optionNo
                        );

                    voteOptionDtos.add(
                        new VoteOptionDto(optionNo, text, votes, percent, isMine)
                    );
                }
            }


            return new PostDto(
                post.getPostId(),
                post.getContent(),
                writer,
                post.getPostType().type.toLowerCase(),
                post.getVoteTitle(),
                voteOptionDtos,
                comments,
                member.getNickname().equals(writer)
            );
        }


        public static List<PostDto> from(List<Post> posts, Member member) {
            return posts.stream()
                .map(post -> PostDto.from(post, member))
                .collect(Collectors.toList());
        }
    }

    @Schema(description = "그룹별 게시글 리스트 DTO")
    public record PostListDto(
        @Schema(description = "그룹 번호", example = "1") int groupNum,
        @Schema(description = "게시글 리스트") List<PostDto> posts
    ) {
        public static PostListDto from(Group g, Member member) {
            return new PostListDto(
                g.getGroupNo(),
                g.getPosts().stream().map(post -> PostDto.from(post, member)).toList()
            );
        }
    }

    @Schema(description = "이벤트 내 전체 그룹 게시글 리스트 DTO")
    public record PostListByGroupDto(
        @Schema(description = "그룹 게시글 목록 리스트") List<PostListDto> lists
    ) {
        public static PostListByGroupDto from(List<Group> groups, Member member) {
            List<PostListDto> listDtos = new ArrayList<>();
            for (Group g : groups) {
                listDtos.add(PostListDto.from(g, member));
            }
            return new PostListByGroupDto(listDtos);
        }
    }
}
