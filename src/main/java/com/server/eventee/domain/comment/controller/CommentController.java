package com.server.eventee.domain.comment.controller;

import com.server.eventee.domain.comment.dto.CommentRequest;
import com.server.eventee.domain.comment.repository.CommentRepository;
import com.server.eventee.domain.comment.service.CommentService;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.BaseResponse;
import com.server.eventee.global.exception.codes.ErrorCode;
import com.server.eventee.global.filter.CurrentMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PatchMapping
    public BaseResponse<?> updateComment(@RequestBody CommentRequest.CommentUpdateDto request){
        try{
            commentService.updateComment(request);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @PostMapping
    public BaseResponse<?> makeComment(@RequestBody CommentRequest.CommentDto request, @CurrentMember Member member){
        try{
            commentService.makeComment(request,member);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @DeleteMapping("/{id}")
    public BaseResponse<?> deleteComment(@PathVariable long id){
        try{
            commentService.deleteComment(id);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }


}
