package com.server.eventee.domain.member.exception;

import com.server.eventee.global.exception.codes.BaseCode;
import com.server.eventee.global.exception.codes.reason.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorStatus implements BaseCode {

  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-0000", "존재하지 않는 회원입니다."),
  MEMBER_SAVE_ERROR(HttpStatus.BAD_REQUEST, "MEMBER-0001", "회원 저장 중 오류가 발생했습니다."),
  MEMBER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "MEMBER-0002", "이미 존재하는 회원입니다."),
  MEMBER_NOT_ACCEPTED(HttpStatus.BAD_REQUEST, "MEMBER-0003", "아직 계정이 활성화되지 않았습니다."),
  MEMBER_NOT_ADMIN(HttpStatus.FORBIDDEN, "MEMBER-0004", "관리자 권한이 필요합니다."),
  MEMBER_NICKNAME_NULL(HttpStatus.BAD_REQUEST, "MEMBER-0005", "닉네임이 비어 있습니다."),
  MEMBER_NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "MEMBER-0006", "이미 존재하는 닉네임입니다.");

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
