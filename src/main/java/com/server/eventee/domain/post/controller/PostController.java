package com.server.eventee.domain.post.controller;

import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.post.dto.PostRequest;
import com.server.eventee.domain.post.dto.PostResponse;
import com.server.eventee.domain.post.service.PostService;
import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.BaseResponse;
import com.server.eventee.global.exception.codes.ErrorCode;
import com.server.eventee.global.filter.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
@Slf4j
@Tag(name = "Post", description = "게시글/투표 API")
public class PostController {

    private final PostService postService;

    @Operation(
        summary = "게시글 생성",
        description = """
                    일반 게시글(TEXT) 또는 투표 게시글(VOTE)을 생성합니다.
                    voteContent는 '항목1_항목2_항목3' 형태로 전달합니다.
                    성공 시 'success' 문자열을 반환합니다.
                    """
    )
    @PostMapping
    public BaseResponse<?> makePost(@RequestBody PostRequest.PostDto request, @CurrentMember Member member){
        try{
            postService.makePost(request,member);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @Operation(
        summary = "게시글 삭제",
        description = """
                    게시글 ID를 받아 해당 게시글을 삭제합니다.
                    작성자 본인만 삭제할 수 있으며, 성공 시 'success' 문자열을 반환합니다.
                    """
    )
    @DeleteMapping("/{id}")
    public BaseResponse<?> deletePost(@PathVariable long id){
        try{
            postService.deletePost(id);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @Operation(
        summary = "게시글 수정",
        description = """
                    게시글 내용을 수정합니다.
                    TEXT/VOTE 타입 모두 수정 가능하며, 성공 시 'success' 문자열을 반환합니다.
                    """
    )
    @PatchMapping
    public BaseResponse<?> updatePost(@RequestBody PostRequest.PostDto request,@CurrentMember Member member){
        try{
            postService.updatePost(request,member);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @Operation(
        summary = "이벤트의 전체 게시글 조회",
        description = """
                    이벤트 ID를 기준으로 모든 그룹의 게시글을 그룹별로 묶어서 가져옵니다.
                    TEXT/VOTE 게시글 및 댓글 정보가 함께 포함됩니다.
                    """
    )
    @GetMapping("/{eventId}")
    public BaseResponse<?> getPostByEvent(@PathVariable Long eventId,@CurrentMember Member member){
        try{
            PostResponse.PostListByGroupDto response = postService.getPostByEvent(eventId,member);
            log.info("response:{}",response);
            return BaseResponse.onSuccess(response);
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @Operation(
        summary = "투표하기",
        description = """
                    투표 게시글에 대해 특정 선택지에 투표합니다.
                    이미 투표한 경우 에러가 발생합니다.
                    성공 시 'success' 문자열을 반환합니다.
                    """
    )
    @PostMapping("/vote")
    public BaseResponse<?> vote(@RequestBody PostRequest.VoteDto request, @CurrentMember Member member){
        try{
            postService.vote(request,member);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }
}
