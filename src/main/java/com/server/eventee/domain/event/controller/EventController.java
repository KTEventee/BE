package com.server.eventee.domain.event.controller;

import com.server.eventee.domain.event.dto.EventRequest;
import com.server.eventee.domain.event.dto.EventResponse;
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

  @Operation(summary = "이벤트 초대 코드로 입장", description = "초대 코드를 입력하여 이벤트에 입장합니다. 이미 참여 중이면 기존 역할(HOST / PARTICIPANT)을 반환합니다.")
  @PostMapping("/join")
  public BaseResponse<EventResponse.JoinResponse> joinEvent(
      @CurrentMember Member member,
      @RequestParam("inviteCode") String inviteCode) {

    EventResponse.JoinResponse response = eventService.joinEvent(member, inviteCode);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }
}
