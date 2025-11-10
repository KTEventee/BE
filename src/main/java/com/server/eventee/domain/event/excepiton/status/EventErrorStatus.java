package com.server.eventee.domain.event.excepiton.status;

import com.server.eventee.global.exception.codes.BaseCode;
import com.server.eventee.global.exception.codes.reason.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EventErrorStatus implements BaseCode {

  // 이벤트 기본 오류
  EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "EVENT-0000", "존재하지 않는 이벤트입니다."),
  EVENT_CREATE_FAILED(HttpStatus.BAD_REQUEST, "EVENT-0001", "이벤트 생성 중 오류가 발생했습니다."),
  EVENT_CLOSED(HttpStatus.BAD_REQUEST, "EVENT-0002", "이미 종료된 이벤트입니다."),
  EVENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "EVENT-0003", "이벤트에 접근할 권한이 없습니다."),
  EVENT_ALREADY_JOINED(HttpStatus.CONFLICT, "EVENT-0004", "이미 해당 이벤트에 참여 중입니다."),
  EVENT_TEAM_COUNT_INVALID(HttpStatus.BAD_REQUEST, "EVENT-0005", "팀 개수가 유효하지 않습니다."),

  // 초대 코드 관련 오류
  INVITE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "EVENT-0100", "존재하지 않는 초대 코드입니다."),
  INVITE_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "EVENT-0101", "만료된 초대 코드입니다."),
  INVITE_CODE_INVALID(HttpStatus.BAD_REQUEST, "EVENT-0102", "유효하지 않은 초대 코드입니다."),

  // 그룹 생성 관련 오류
  GROUP_CREATE_FAILED(HttpStatus.BAD_REQUEST, "EVENT-0200", "이벤트 그룹 생성 중 오류가 발생했습니다."),
  GROUP_COUNT_MISMATCH(HttpStatus.BAD_REQUEST, "EVENT-0201", "그룹 수가 이벤트 설정과 일치하지 않습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  @Override
  public Reason.ReasonDto getReasonHttpStatus() {
    return Reason.ReasonDto.builder()
        .message(message)
        .code(code)
        .isSuccess(false)
        .httpStatus(httpStatus)
        .build();
  }
}
