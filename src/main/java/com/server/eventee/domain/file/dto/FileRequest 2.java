package com.server.eventee.domain.file.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FileRequest {

  /* -----------------------------------------------------
      Presigned URL 발급 요청 (PUT 업로드 Intent)
      type: PROFILE / GROUP / EVENT / POST / COMMENT
      refId: 어떤 엔티티의 이미지인지
  ----------------------------------------------------- */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class FileUploadRequest {

    @NotNull
    private String type;

    @NotNull
    private Long refId;

    @NotNull
    private String contentType;

    @NotNull
    private Long contentLength;
  }

  /* -----------------------------------------------------
      업로드 확정 요청 (/confirm)
      fileUrl: S3 공개 URL
  ----------------------------------------------------- */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class FileConfirmRequest {

    @NotNull
    private String type;

    @NotNull
    private Long refId;

    @NotNull
    private String fileUrl;
  }

  /* -----------------------------------------------------
      파일 삭제 요청
  ----------------------------------------------------- */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class FileDeleteRequest {

    @NotNull
    private String type;

    @NotNull
    private Long refId;
  }
}
