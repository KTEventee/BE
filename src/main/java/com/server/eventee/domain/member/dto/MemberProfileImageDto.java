package com.server.eventee.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Presigned URL (PUT) 기반 프로필 이미지 업로드 관련 DTO 묶음 클래스
 */
public class MemberProfileImageDto {

  /**
   * Presigned URL 발급 요청 DTO
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "Presigned URL 발급 요청 DTO")
  public static class UploadIntentRequest {

    @Schema(description = "업로드할 파일의 MIME 타입", example = "image/jpeg")
    @NotBlank
    private String contentType;

    @Schema(description = "업로드할 파일의 크기(byte)", example = "524288")
    @Min(1)
    private long contentLength;
  }

  /**
   * Presigned URL 응답 DTO
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "Presigned URL 응답 DTO")
  public static class PresignedUrlResponse {

    @Schema(description = "S3에 직접 PUT 업로드할 URL",
        example = "https://bucket.s3.ap-northeast-2.amazonaws.com/profiles/1/uuid.jpg")
    private String url;

    @Schema(description = "S3 객체 키 (confirm 시 사용)",
        example = "profiles/1/uuid.jpg")
    private String key;

    @Schema(description = "URL 유효기간(초)", example = "300")
    private long expiresIn;
  }

  /**
   * 업로드 완료 후 서버 반영 요청 DTO
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "업로드 완료 후 서버 반영 요청 DTO")
  public static class ConfirmUploadRequest {

    @Schema(description = "업로드된 S3 객체 키", example = "profiles/1/uuid.jpg")
    @NotBlank
    private String key;

    @Schema(description = "파일 MIME 타입", example = "image/jpeg")
    @NotBlank
    private String contentType;

    @Schema(description = "파일 크기(byte)", example = "524288")
    @NotNull
    @Min(1)
    private Long size;
  }

  /**
   * 프로필 이미지 삭제 응답 DTO
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "프로필 이미지 삭제 응답 DTO")
  public static class DeleteImageResponse {

    @Schema(description = "삭제된 이미지 URL",
        example = "https://cdn.example.com/profiles/1/old.jpg")
    private String previousUrl;

    @Schema(description = "삭제 상태 (deleted, not_found)", example = "deleted")
    private String status;
  }
}
