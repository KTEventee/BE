package com.server.eventee.domain.group.controller;

import com.server.eventee.domain.group.dto.GroupReqeust;
import com.server.eventee.domain.group.dto.GroupResponse;
import com.server.eventee.domain.group.service.GroupService;
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
@RequestMapping("/api/v1/group")
@Slf4j
@Tag(name = "Group API", description = "그룹(조) 생성, 수정, 이동, 조회 관련 API")
public class GroupController {

    private final GroupService groupService;


    @Operation(
            summary = "관리자용 그룹 추가 생성",
            description = """
                    관리자가 새로운 그룹(조)을 생성할 때 사용하는 API입니다.
                    기본 조 외에 추가 조를 만들 때 사용됩니다.
                    """
    )
    @PostMapping("/admin")
    public BaseResponse<String> createAdditionalGroup(@RequestBody GroupReqeust.GroupCreateDto request
            ,@CurrentMember Member member
    ){
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

    @Operation(
            summary = "특정 그룹에 참여",
            description = """
                    사용자가 특정 그룹에 참여(입장)하는 API입니다.
                    이미 다른 그룹에 속해 있다면 오류가 발생할 수 있습니다.
                    """
    )
    @PostMapping("/enter/{id}")
    public BaseResponse<String> enterGroup(@PathVariable long id, @CurrentMember Member member){
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

    @Operation(
            summary = "그룹 탈퇴",
            description = """
                    사용자가 현재 속한 그룹에서 탈퇴하는 API입니다.
                    """
    )
    @PostMapping("/leave/{id}")
    public BaseResponse<String> leaveGroup(@PathVariable long id,@CurrentMember Member member){
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

    @Operation(
            summary = "사용자 그룹(조) 이동",
            description = """
                    사용자를 기존 그룹에서 다른 그룹으로 이동시키는 관리자 API입니다.
                    beforeGroup → afterGroup 순으로 이동됩니다.
                    """
    )
    @PostMapping("/move")
    public BaseResponse<String> moveGroup(@RequestBody GroupReqeust.GroupMoveDto request,@CurrentMember Member member){
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

    @Operation(
            summary = "그룹 정보 수정",
            description = """
                    그룹의 이름, 설명, 이미지, 리더 정보를 수정하는 API입니다.
                    """
    )
    @PutMapping
    public BaseResponse<String> updateGroup(@RequestBody GroupReqeust.GroupUpdateDto request){
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

    @Operation(
            summary = "그룹 리더 변경",
            description = """
                    해당 그룹의 리더(대표자) 정보를 변경하는 API입니다.
                    """
    )
    @PatchMapping("/leader")
    public BaseResponse<String> updateGroupLeader(@RequestBody GroupReqeust.GroupUpdateLeaderDto request){
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

    @Operation(
            summary = "그룹 삭제",
            description = """
                    특정 그룹을 삭제하는 관리자용 API입니다.
                    삭제 시 isDeleted = true 로 처리되며 실제 DB에서 제거되지는 않습니다.
                    """
    )
    @DeleteMapping("/{id}")
    public BaseResponse<String> deleteGroup(@PathVariable Long id){
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

    @Operation(
            summary = "이벤트 기준 그룹 조회",
            description = """
                    특정 eventId 에 속한 그룹 목록을 조회하는 API입니다.
                    현재 로그인한 사용자의 그룹(myGroup)과,
                    나머지 그룹(otherGroup)을 구분하여 반환합니다.
                    """
    )
    @GetMapping("/{eventId}")
    public BaseResponse<GroupResponse.ListDto> getGroupByEvent(@PathVariable Long eventId, @CurrentMember Member member){
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
