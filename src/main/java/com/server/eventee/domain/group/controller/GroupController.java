package com.server.eventee.domain.group.controller;

import com.server.eventee.domain.group.dto.GroupReqeust;
import com.server.eventee.domain.group.service.GroupService;
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
@RequestMapping("/api/v1/group")
@Slf4j
public class GroupController {

    private final GroupService groupService;


    @PostMapping
    public BaseResponse<?> createAdditionalGroup(@RequestBody GroupReqeust.GroupCreateDto request,@CurrentMember Member member){
        try{
            groupService.createAdditionalGroup(request,member);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @PostMapping("/enter/{id}")
    public BaseResponse<?> enterGroup(@PathVariable long id, @CurrentMember Member member){
        try{
            groupService.enterGroup(id,member);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @PostMapping("/leave/{id}")
    public BaseResponse<?> leaveGroup(@PathVariable long id,@CurrentMember Member member){
        try{
            groupService.leaveGroup(id,member);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @PostMapping("/move")
    public BaseResponse<?> moveGroup(@RequestBody GroupReqeust.GroupMoveDto request,@CurrentMember Member member){
        try{
            groupService.moveGroup(request,member);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @PutMapping
    public BaseResponse<?> updateGroup(@PathVariable GroupReqeust.GroupUpdateDto request){
        try{
            groupService.updateGroup(request);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @PatchMapping("/leader")
    public BaseResponse<?> updateGroupLeader(@RequestBody GroupReqeust.GroupUpdateLeaderDto request){
        try{
            groupService.updateLeader(request);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @DeleteMapping("/{id}")
    public BaseResponse<?> deleteGroup(@PathVariable Long id){
        try{
            groupService.deleteGroup(id);
            return BaseResponse.onSuccess("success");
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @GetMapping("/{eventId}")
    public BaseResponse<?> getGroupByEvent(@PathVariable Long eventId,@CurrentMember Member member){
        try{
            return BaseResponse.onSuccess(groupService.getGroupByEvent(eventId,member));
        }catch(BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

}
