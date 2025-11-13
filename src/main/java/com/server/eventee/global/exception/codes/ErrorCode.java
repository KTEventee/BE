package com.server.eventee.global.exception.codes;

import com.server.eventee.global.exception.codes.reason.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    /**
     * ******************************* Global Error CodeList ***************************************
     * HTTP Status Code
     * 400 : Bad Request
     * 401 : Unauthorized
     * 403 : Forbidden
     * 404 : Not Found
     * 500 : Internal Server Error
     * *********************************************************************************************
     */
    // 잘못된 서버 요청
    BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "G001", "Bad Request Exception"),

    // @RequestBody 데이터 미 존재
    REQUEST_BODY_MISSING_ERROR(HttpStatus.BAD_REQUEST, "G002", "Required request body is missing"),

    // 유효하지 않은 타입
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "G003", " Invalid Type Value"),

    // Request Parameter 로 데이터가 전달되지 않을 경우
    MISSING_REQUEST_PARAMETER_ERROR(HttpStatus.BAD_REQUEST, "G004", "Missing Servlet RequestParameter Exception"),

    // 입력/출력 값이 유효하지 않음
    IO_ERROR(HttpStatus.BAD_REQUEST, "G005", "I/O Exception"),

    // com.google.gson JSON 파싱 실패
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "G006", "JsonParseException"),

    // com.fasterxml.jackson.core Processing Error
    JACKSON_PROCESS_ERROR(HttpStatus.BAD_REQUEST, "G007", "com.fasterxml.jackson.core Exception"),

    // 권한이 없음
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "G008", "Forbidden Exception"),

    // 서버로 요청한 리소스가 존재하지 않음
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "G009", "Not Found Exception"),

    // NULL Point Exception 발생
    NULL_POINT_ERROR(HttpStatus.NOT_FOUND, "G010", "Null Point Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_ERROR(HttpStatus.NOT_FOUND, "G011", "handle Validation Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_HEADER_ERROR(HttpStatus.NOT_FOUND, "G012", "Header에 데이터가 존재하지 않는 경우 "),

    //post에러
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST,"POST-0000","게시글을 찾을 수 없음"),
    POST_TYPE_NOT_VALID(HttpStatus.BAD_REQUEST, "POST-0001", "해당 타입은 존재하지 않는 타입입니다."),
    POST_TYPE_NOT_VOTE(HttpStatus.BAD_REQUEST, "POST-0001", "해당 타입은 투표용 게시글이 아닙니다."),

    //댓글 에러
    COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "COMMENT-0000", "해당 댓글을 찾을 수 없습니다."),
    
    //자잘한 에러
    SEARCH_KEYWORD_TOO_SHORT(HttpStatus.BAD_REQUEST, "KEYWORD-0000", "검색어는 3글자부터 입력하세요."),

    //바인딩 에러
    BINDING_ERROR(HttpStatus.BAD_REQUEST, "BINDING-0000", "바인딩에 실패했습니다."),

    //로그인 에러
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "LOGIN-0000", "이메일이 잘못됨"),
    PASSWORD_ERROR(HttpStatus.BAD_REQUEST, "LOGIN-0001", "잘못된 비밀번호입니다."),
    EXIST_EMAIL(HttpStatus.BAD_REQUEST, "LOGIN-0003", "이미 존재하는 회원입니다."),

    //common
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-0000", "잘못된 요청입니다."),

    //event
    EVENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "EVENT-0000", "해당 이벤트 ID를 찾을 수 없습니다."),

    //GroupError
    GROUP_NOT_FOUND(HttpStatus.BAD_REQUEST, "GROUP-0000", "해당 ID를 가진 그룹을 찾을 수 없습니다."),
    
    //JWT error
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN-0000", "AccessToken 기간 만료됨"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN-0001", "토큰이 올바르지 않습니다."),
    EMPTY_TOKEN_PROVIDED(HttpStatus.UNAUTHORIZED, "TOKEN-0002", "토큰 텅텅"),
    REFRESH_TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "TOKEN-0003", "리프레시 토큰이 올바르지 않음"),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-0000", "존재하지 않는 회원입니다."),
    MEMBER_SAVE_ERROR(HttpStatus.BAD_REQUEST,"MEMBER-0001","member save error"),
    MEMBER_ALREADY_EXIST(HttpStatus.BAD_REQUEST,"MEMBER-0002","member already exist"),
    MEMBER_NOT_ACCEPTED(HttpStatus.BAD_REQUEST,"MEMBER-0003","아직 계정이 활성화되지 않았습니다."),
    MEMBER_NOT_ADMIN(HttpStatus.BAD_REQUEST,"MEMBER-0004","Meber is not ADMIN"),

    // 5xx : server error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER-0000", "서버 에러");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public Reason.ReasonDto getReasonHttpStatus() {
        return Reason.ReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
