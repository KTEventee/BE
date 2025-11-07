package com.server.eventee.global.exception.handler;

import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.BaseResponse;
import com.server.eventee.global.exception.codes.BaseCode;
import com.server.eventee.global.exception.codes.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  // 비즈니스 로직에서 발생하는 커스텀 예외 처리
  @ExceptionHandler(BaseException.class)
  public ResponseEntity<Object> handleBaseException(BaseException e, HttpServletRequest request) {
    log.warn("[BaseException] {} - {}", e.getCode().getReasonHttpStatus().getCode(), e.getMessage());
    BaseCode baseCode = e.getCode();
    return ResponseEntity
        .status(baseCode.getReasonHttpStatus().getHttpStatus())
        .body(BaseResponse.onFailure(baseCode, null));
  }

  // @Valid, @RequestBody 등에서 발생하는 DTO 검증 실패 처리
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> {
      String field = error.getField();
      String message = Optional.ofNullable(error.getDefaultMessage()).orElse("");
      errors.merge(field, message, (existing, current) -> existing + ", " + current);
    });

    BaseResponse<Object> body =
        BaseResponse.onFailure(ErrorCode.NOT_VALID_ERROR.getCode(), ErrorCode.NOT_VALID_ERROR.getMessage(), errors);

    return handleExceptionInternal(ex, body, headers, ErrorCode.NOT_VALID_ERROR.getHttpStatus(), request);
  }

  // @RequestParam 누락 시 발생
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    log.warn("[MissingServletRequestParameter] {}", ex.getParameterName());
    BaseResponse<Object> body =
        BaseResponse.onFailure(ErrorCode.MISSING_REQUEST_PARAMETER_ERROR.getCode(),
            String.format("필수 파라미터가 누락되었습니다: '%s'", ex.getParameterName()), null);

    return handleExceptionInternal(ex, body, headers, ErrorCode.MISSING_REQUEST_PARAMETER_ERROR.getHttpStatus(), request);
  }

  // @PathVariable 누락 시 발생
  @Override
  protected ResponseEntity<Object> handleMissingPathVariable(
      MissingPathVariableException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    log.warn("[MissingPathVariable] {}", ex.getVariableName());
    BaseResponse<Object> body =
        BaseResponse.onFailure(ErrorCode.NOT_VALID_ERROR.getCode(),
            String.format("필수 경로 변수가 누락되었습니다: '%s'", ex.getVariableName()), null);

    return handleExceptionInternal(ex, body, headers, ErrorCode.NOT_VALID_ERROR.getHttpStatus(), request);
  }

  // 지원하지 않는 HTTP 메서드 사용
  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    BaseResponse<Object> body =
        BaseResponse.onFailure(ErrorCode._BAD_REQUEST.getCode(),
            String.format("지원하지 않는 HTTP 메서드입니다: %s", ex.getMethod()), null);

    return handleExceptionInternal(ex, body, headers, ErrorCode._BAD_REQUEST.getHttpStatus(), request);
  }

  // 지원하지 않는 Content-Type 요청
  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    BaseResponse<Object> body =
        BaseResponse.onFailure(ErrorCode._BAD_REQUEST.getCode(),
            String.format("지원하지 않는 Content-Type: %s", ex.getContentType()), null);

    return handleExceptionInternal(ex, body, headers, ErrorCode._BAD_REQUEST.getHttpStatus(), request);
  }

  // @Validated, @RequestParam 검증 실패 (ConstraintViolation)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e, WebRequest request) {
    String message = e.getConstraintViolations().stream()
        .map(v -> v.getMessage())
        .findFirst()
        .orElse("잘못된 요청입니다.");

    BaseResponse<Object> body =
        BaseResponse.onFailure(ErrorCode._BAD_REQUEST.getCode(), message, null);

    return handleExceptionInternal(e, body, HttpHeaders.EMPTY, ErrorCode._BAD_REQUEST.getHttpStatus(), request);
  }

  //처리되지 않은 모든 예외
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleUnknownException(Exception e, WebRequest request) {
    log.error("[Unhandled Exception]", e);

    BaseResponse<Object> body =
        BaseResponse.onFailure(ErrorCode._INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode._INTERNAL_SERVER_ERROR.getMessage(), null);

    return handleExceptionInternal(e, body, HttpHeaders.EMPTY,
        ErrorCode._INTERNAL_SERVER_ERROR.getHttpStatus(), request);
  }
}
