package com.server.eventee.domain.event.controller;

import com.server.eventee.domain.event.dto.EventRequest;
import com.server.eventee.domain.event.dto.EventResponse;
import com.server.eventee.domain.event.dto.MemberListDto;
import com.server.eventee.domain.event.service.EventService;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.global.exception.BaseResponse;
import com.server.eventee.global.exception.codes.SuccessCode;
import com.server.eventee.global.filter.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Event", description = "이벤트 생성 및 입장 관련 API")
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {

  private final EventService eventService;

  @Operation(summary = "이벤트 생성", description = "새로운 이벤트를 생성합니다. 지정한 팀 수만큼 그룹이 자동 생성되며, 생성자는 HOST 역할로 등록됩니다.")
  @PostMapping
  public BaseResponse<EventResponse.CreateResponse> createEvent(
      @CurrentMember Member member,
      @Valid @RequestBody EventRequest.CreateRequest request) {

    EventResponse.CreateResponse response = eventService.createEvent(member, request);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  @Operation(
      summary = "이벤트 초대 코드 및 비밀번호로 입장",
      description = """
        초대 코드와 비밀번호를 검증한 뒤 이벤트에 입장합니다.
        이미 참여 중이면 기존 역할(HOST / PARTICIPANT)을 반환하고,
        처음 입장하는 경우 자동으로 PARTICIPANT로 등록됩니다.
        """
  )
  @PostMapping("/join")
  public BaseResponse<EventResponse.JoinResponse> joinEvent(
      @CurrentMember Member member,
      @Valid @RequestBody EventRequest.JoinRequest request
  ) {
    EventResponse.JoinResponse response = eventService.joinEvent(member, request);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  @Operation(
      summary = "이벤트별 그룹 목록 조회 (로그인 사용자 전용)",
      description = """
        로그인한 사용자가 속한 이벤트의 기본 정보와 모든 그룹(조)의 요약 정보를 조회합니다.
        이벤트에 참여하지 않은 사용자는 접근할 수 없습니다.
        프론트는 이 응답을 기반으로 초기화면(이벤트 정보 + 그룹 카드)을 구성합니다.
        """
  )
  @GetMapping("/{eventId}/groups")
  public BaseResponse<EventResponse.EventWithGroupsResponse> getEventGroups(
      @CurrentMember Member member,
      @PathVariable Long eventId
  ) {
    EventResponse.EventWithGroupsResponse response = eventService.getEventGroups(member, eventId);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  @Operation(
      summary = "그룹별 포스트 및 투표 조회 (로그인 사용자 전용)",
      description = "특정 이벤트 내 하나의 그룹(조)에 속한 게시글(Post), 댓글(Comment), 투표(VoteLog) 데이터를 조회합니다."
  )
  @GetMapping("/{eventId}/groups/{groupId}/posts")
  public BaseResponse<EventResponse.GroupPostsResponse> getGroupPosts(
      @CurrentMember Member member,
      @PathVariable Long eventId,
      @PathVariable Long groupId
  ) {
    EventResponse.GroupPostsResponse response = eventService.getGroupPosts(member, eventId, groupId);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  @Operation(
      summary = "초대 코드 유효성 검증",
      description = """
        초대 코드만으로 이벤트 존재 여부를 확인합니다.
        존재하면 valid=true 와 이벤트 ID를 반환합니다.
        비밀번호 검증은 하지 않습니다.
        """
  )
  @GetMapping("/validate")
  public BaseResponse<EventResponse.InviteCodeValidateResponse> validateInviteCode(
      @RequestParam String code
  ) {
    EventResponse.InviteCodeValidateResponse response = eventService.validateInviteCode(code);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  @Operation(
      summary = "초대 코드 + 비밀번호 검증",
      description = """
        초대 코드와 비밀번호가 유효한지 확인합니다.
        이벤트 입장은 처리하지 않고, 단순히 비밀번호 검증만 수행합니다.
        이 API는 닉네임 입력 전에 비밀번호를 미리 검증하는 용도로 사용합니다.
        """
  )
  @PostMapping("/verify")
  public BaseResponse<EventResponse.EventPasswordVerifyResponse> verifyEventPassword(
      @Valid @RequestBody EventRequest.PasswordVerifyRequest request
  ) {
    EventResponse.EventPasswordVerifyResponse response = eventService.verifyEventPassword(request);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  @GetMapping("/admin/members")
  public BaseResponse<List<MemberListDto.MemberDto>> getMembers(@RequestParam Long eventId){
    return BaseResponse.onSuccess(eventService.getMembersByEvent(eventId));
  }

  @PostMapping("/admin/ban")
  public BaseResponse<String> kickMember(@RequestBody EventRequest.KickMemberRequest request){
    eventService.kickMember(request);
    return BaseResponse.onSuccess("success");
  }

}
