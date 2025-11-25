package com.server.eventee.domain.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Event", description = "이벤트 생성 및 입장 관련 API")
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@Slf4j
public class EventController {

  private final EventService eventService;
  private final ObjectMapper mapper = new ObjectMapper();

  private void logResponse(Object response) {
    try {
      log.info("[Event API Response] {}", mapper.writeValueAsString(response));
    } catch (Exception e) {
      log.warn("Response log convert error: {}", e.getMessage());
    }
  }

  @Operation(summary = "이벤트 생성")
  @PostMapping
  public BaseResponse<EventResponse.CreateResponse> createEvent(
      @CurrentMember Member member,
      @Valid @RequestBody EventRequest.CreateRequest request) {

    EventResponse.CreateResponse response = eventService.createEvent(member, request);
    logResponse(response);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  @Operation(summary = "이벤트 입장")
  @PostMapping("/join")
  public BaseResponse<EventResponse.JoinResponse> joinEvent(
      @CurrentMember Member member,
      @Valid @RequestBody EventRequest.JoinRequest request
  ) {
    EventResponse.JoinResponse response = eventService.joinEvent(member, request);
    logResponse(response);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  @Operation(summary = "이벤트 그룹 목록 조회")
  @GetMapping("/{eventId}/groups")
  public BaseResponse<EventResponse.EventWithGroupsResponse> getEventGroups(
      @CurrentMember Member member,
      @PathVariable Long eventId
  ) {
    EventResponse.EventWithGroupsResponse response = eventService.getEventGroups(member, eventId);
    logResponse(response);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  @Operation(summary = "그룹별 포스트 조회")
  @GetMapping("/{eventId}/groups/{groupId}/posts")
  public BaseResponse<EventResponse.GroupPostsResponse> getGroupPosts(
      @CurrentMember Member member,
      @PathVariable Long eventId,
      @PathVariable Long groupId
  ) {
    EventResponse.GroupPostsResponse response = eventService.getGroupPosts(member, eventId, groupId);
    logResponse(response);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  @Operation(summary = "초대 코드 검증")
  @GetMapping("/validate")
  public BaseResponse<EventResponse.InviteCodeValidateResponse> validateInviteCode(
      @RequestParam String code
  ) {
    EventResponse.InviteCodeValidateResponse response = eventService.validateInviteCode(code);
    logResponse(response);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  @Operation(summary = "비밀번호 검증")
  @PostMapping("/verify")
  public BaseResponse<EventResponse.EventPasswordVerifyResponse> verifyEventPassword(
      @Valid @RequestBody EventRequest.PasswordVerifyRequest request
  ) {
    EventResponse.EventPasswordVerifyResponse response = eventService.verifyEventPassword(request);
    logResponse(response);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  @Operation(summary = "이벤트 멤버 가져오기")
  @GetMapping("/admin/members")
  public BaseResponse<List<MemberListDto.MemberDto>> getMembers(
      @RequestParam Long eventId,
      @CurrentMember Member member){

    List<MemberListDto.MemberDto> response = eventService.getMembersByEvent(eventId,member);
    logResponse(response);
    return BaseResponse.onSuccess(response);
  }

  @Operation(summary = "사용자 강퇴")
  @PostMapping("/admin/ban")
  public BaseResponse<String> kickMember(
      @RequestBody EventRequest.KickMemberRequest request,
    @CurrentMember Member member){
    eventService.kickMember(request, member);
    logResponse("success");
    return BaseResponse.onSuccess("success");
  }
}
