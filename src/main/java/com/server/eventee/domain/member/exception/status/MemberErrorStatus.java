package com.server.eventee.domain.member.exception.status;

import com.server.eventee.global.exception.codes.BaseCode;
import com.server.eventee.global.exception.codes.reason.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorStatus implements BaseCode {

  // 기본 회원 관련 오류
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-0000", "존재하지 않는 회원입니다."),
  MEMBER_SAVE_ERROR(HttpStatus.BAD_REQUEST, "MEMBER-0001", "회원 저장 중 오류가 발생했습니다."),
  MEMBER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "MEMBER-0002", "이미 존재하는 회원입니다."),
  MEMBER_NOT_ACCEPTED(HttpStatus.BAD_REQUEST, "MEMBER-0003", "아직 계정이 활성화되지 않았습니다."),
  MEMBER_NOT_ADMIN(HttpStatus.FORBIDDEN, "MEMBER-0004", "관리자 권한이 필요합니다."),
  MEMBER_NICKNAME_NULL(HttpStatus.BAD_REQUEST, "MEMBER-0005", "닉네임이 비어 있습니다."),
  MEMBER_NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "MEMBER-0006", "이미 존재하는 닉네임입니다."),

  // 프로필 이미지 관련 오류
  MEMBER_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-0100", "프로필 이미지가 존재하지 않습니다."),
  MEMBER_IMAGE_INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "MEMBER-0101", "허용되지 않은 이미지 형식입니다."),
  MEMBER_IMAGE_INVALID_SIZE(HttpStatus.BAD_REQUEST, "MEMBER-0102", "이미지 파일 크기가 유효하지 않습니다."),
  MEMBER_IMAGE_INVALID_KEY(HttpStatus.BAD_REQUEST, "MEMBER-0103", "잘못된 이미지 키 형식입니다."),
  MEMBER_IMAGE_CONTENT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "MEMBER-0104", "이미지 Content-Type이 일치하지 않습니다.");

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
