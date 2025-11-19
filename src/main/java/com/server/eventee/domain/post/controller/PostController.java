package com.server.eventee.domain.post.controller;

import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.post.dto.PostRequest;
import com.server.eventee.domain.post.dto.PostResponse;
import com.server.eventee.domain.post.service.PostService;
import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.BaseResponse;
import com.server.eventee.global.exception.codes.ErrorCode;
import com.server.eventee.global.filter.CurrentMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
@Slf4j
public class PostController {

    private final PostService postService;


    //NOTE 투표는 요소1_요소2_요소3 같은 형태로 받기
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

    @PostMapping("/vote")
    public BaseResponse<?> vote(@RequestBody PostRequest.VoteDto request, @CurrentMember Member member){
        try{
            return BaseResponse.onSuccess(postService.vote(request,member));
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }


}
