package com.server.eventee.domain.post.controller;

import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.post.dto.PostRequest;
import com.server.eventee.domain.post.dto.PostResponse;
import com.server.eventee.domain.post.dto.VoteLogResponseDto;
import com.server.eventee.domain.post.model.Post;
import com.server.eventee.domain.post.service.PostService;
import com.server.eventee.global.exception.BaseResponse;
import com.server.eventee.global.filter.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
@Slf4j
@Tag(name = "Post", description = "게시글 / 투표 API")
public class PostController {

    private final PostService postService;


    @Operation(
        summary = "게시글 생성",
        description = """
                    TEXT 또는 VOTE 타입의 게시글을 생성합니다.
                    
                    - type: "TEXT" | "VOTE"
                    - voteContent: "옵션1_옵션2_옵션3"
                    
                    반환값은 생성된 게시글의 전체 정보를 포함합니다.
                    """
    )
    @PostMapping
    public BaseResponse<PostResponse.PostDto> createPost(
        @RequestBody @Schema(description = "게시글 생성 요청 DTO") PostRequest.PostDto request,
        @CurrentMember Member member
    ) {
        Post post = postService.makePost(request, member);
        return BaseResponse.onSuccess(PostResponse.PostDto.from(post, member));
    }


    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    public BaseResponse<String> deletePost(
        @PathVariable @Schema(description = "게시글 ID") long postId
    ) {
        postService.deletePost(postId);
        return BaseResponse.onSuccess("success");
    }


    @Operation(
        summary = "게시글 수정",
        description = "TEXT/VOTE 타입의 게시글 내용을 수정합니다."
    )
    @PatchMapping("/{postId}")
    public BaseResponse<PostResponse.PostDto> updatePost(
        @PathVariable @Schema(description = "게시글 ID") Long postId,
        @RequestBody @Schema(description = "게시글 수정 요청 DTO") PostRequest.PostDto request,
        @CurrentMember Member member
    ) {
        PostResponse.PostDto updated = postService.updatePost(request, member, postId);
        return BaseResponse.onSuccess(updated);
    }


    @Operation(
        summary = "이벤트 전체 게시글 조회",
        description = """
                    이벤트 내 모든 그룹의 게시글을 가져옵니다.
                    TEXT/VOTE 게시글과 댓글 데이터 포함.
                    """
    )
    @GetMapping("/{eventId}")
    public BaseResponse<PostResponse.PostListByGroupDto> getPostByEvent(
        @PathVariable @Schema(description = "이벤트 ID") Long eventId,
        @CurrentMember Member member
    ) {
        return BaseResponse.onSuccess(postService.getPostByEvent(eventId, member));
    }


    @Operation(
        summary = "투표하기",
        description = """
                    투표 게시글에 대해 특정 옵션에 투표합니다.
                    
                    이미 투표한 경우 에러 발생.
                    """
    )
    @PostMapping("/vote")
    public BaseResponse<VoteLogResponseDto> vote(
        @RequestBody @Schema(description = "투표 요청 DTO") PostRequest.VoteDto request,
        @CurrentMember Member member
    ) {
        return BaseResponse.onSuccess(postService.vote(request, member));
    }


    @Operation(
        summary = "관리자 게시글 등록",
        description = """
                    특정 그룹 번호들에 동일한 게시글을 일괄 등록합니다.
                    groupNums 예시: "1_3_4"
                    """
    )
    @PostMapping("/admin")
    public BaseResponse<String> adminPost(
        @RequestBody @Schema(description = "관리자 게시글 요청 DTO") PostRequest.AdminPostDto request,
        @CurrentMember Member member
    ) {
        postService.adminPost(request, member);
        return BaseResponse.onSuccess("success");
    }
}
