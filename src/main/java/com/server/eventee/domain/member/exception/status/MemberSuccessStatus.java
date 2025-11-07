package com.server.eventee.domain.member.exception.status;

import com.server.eventee.global.exception.codes.BaseCode;
import com.server.eventee.global.exception.codes.reason.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Member 도메인 관련 성공 상태 코드 모음
 */
@Getter
@AllArgsConstructor
public enum MemberSuccessStatus implements BaseCode {

  _NICKNAME_OK(HttpStatus.OK, "MEMBER-2000", "사용 가능한 닉네임입니다."),
  _MEMBER_REGISTERED(HttpStatus.CREATED, "MEMBER-2001", "회원 가입이 완료되었습니다."),
  _MEMBER_UPDATED(HttpStatus.OK, "MEMBER-2002", "회원 정보가 수정되었습니다."),
  _MEMBER_DELETED(HttpStatus.OK, "MEMBER-2003", "회원 탈퇴가 완료되었습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  @Override
  public Reason.ReasonDto getReasonHttpStatus() {
    return Reason.ReasonDto.builder()
        .message(message)
        .code(code)
        .isSuccess(true)
        .httpStatus(httpStatus)
        .build();
  }
}
