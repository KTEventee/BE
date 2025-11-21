package com.server.eventee.domain.comment.controller;

import com.server.eventee.domain.comment.dto.CommentRequest;
import com.server.eventee.domain.comment.service.CommentService;
import com.server.eventee.domain.member.model.Member;
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
@RequestMapping("/api/v1/comment")
@Tag(name = "Comment", description = "댓글 API")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @Operation(
        summary = "댓글 수정",
        description = "기존 댓글 내용을 수정합니다. 작성자 본인만 수정할 수 있습니다."
    )
    @PatchMapping
    public BaseResponse<?> updateComment(@RequestBody CommentRequest.CommentUpdateDto request){
        try{
            commentService.updateComment(request);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(), null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST, null);
        }
    }

    @Operation(
        summary = "댓글 생성",
        description = "새로운 댓글을 작성합니다. 로그인한 사용자 정보는 @CurrentMember에서 주입됩니다."
    )
    @PostMapping
    public BaseResponse<?> makeComment(
        @RequestBody CommentRequest.CommentDto request,
        @CurrentMember Member member
    ){
        try{
            commentService.makeComment(request, member);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(), null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST, null);
        }
    }

    @Operation(
        summary = "댓글 삭제",
        description = "특정 댓글을 삭제합니다. 작성자 또는 관리자만 삭제 가능합니다."
    )
    @DeleteMapping("/{commentId}")
    public BaseResponse<?> deleteComment(@PathVariable long commentId){
        try{
            commentService.deleteComment(commentId);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(), null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST, null);
        }
    }
}
