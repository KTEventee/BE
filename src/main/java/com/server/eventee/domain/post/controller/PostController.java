package com.server.eventee.domain.post.controller;

import com.server.eventee.domain.post.dto.PostRequest;
import com.server.eventee.domain.post.service.PostService;
import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.BaseResponse;
import com.server.eventee.global.exception.codes.ErrorCode;
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
    public BaseResponse<?> makePost(@RequestBody PostRequest.PostDto request){
        try{
            postService.makePost(request);
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
    public BaseResponse<?> updatePost(@RequestBody PostRequest.PostDto request){
        try{
            postService.updatePost(request);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @GetMapping("/{evnetId}")
    public BaseResponse<?> getPostByEvent(@PathVariable Long evnetId){
        try{
            postService.getPostByEvent(evnetId);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @PostMapping("/vote")
    public BaseResponse<?> vote(@RequestBody PostRequest.VoteDto request){
        try{
            postService.vote(request);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }


}
