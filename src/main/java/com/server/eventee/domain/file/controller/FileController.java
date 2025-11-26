package com.server.eventee.domain.file.controller;

import com.server.eventee.domain.file.dto.FileRequest;
import com.server.eventee.domain.file.dto.FileUploadResponse;
import com.server.eventee.domain.file.service.FileService;
import com.server.eventee.domain.member.dto.MemberProfileImageDto;
import com.server.eventee.domain.member.dto.MemberProfileImageDto.PresignedUrlResponse;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.global.exception.BaseResponse;
import com.server.eventee.global.exception.codes.SuccessCode;
import com.server.eventee.global.filter.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "File", description = "이미지 관련 API")
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
public class FileController {
  private final FileService fileService;

  @Operation(
      summary = "이미지 Presigned URL 발급 (PUT)",
      description = """
          S3에 직접 PUT 업로드할 URL을 발급합니다.
          프론트는 해당 URL로 이미지를 업로드한 뒤 /confirm을 호출해야 합니다.
          """
  )
  @PostMapping("/presigned-url")
  public BaseResponse<FileUploadResponse> createPresignedUrl(
      @CurrentMember Member member,
      @Valid @RequestBody FileRequest.FileUploadRequest request) {

    FileUploadResponse response = fileService.createPresignedUrl(request);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  //업로드 확정 (PUT 완료 후 호출)
  @Operation(
      summary = "프로필 이미지 업로드 확정",
      description = "PUT 업로드가 완료된 이미지를 확인하고 회원 프로필에 반영합니다."
  )
  @PostMapping("/confirm")
  public BaseResponse<String> confirmProfileImage(
      @CurrentMember Member member,
      @Valid @RequestBody FileRequest.FileConfirmRequest request) {

    String imageUrl = fileService.confirmUpload(member, request);
    return BaseResponse.of(SuccessCode.SUCCESS, imageUrl);
  }

  //프로필 이미지 삭제
  @Operation(
      summary = "프로필 이미지 삭제",
      description = "S3에서 기존 프로필 이미지를 삭제하고 회원 프로필을 초기화합니다."
  )
  @DeleteMapping("/profile-image")
  public BaseResponse<String> deleteProfileImage(
      @CurrentMember Member member,
      @Valid @RequestBody FileRequest.FileDeleteRequest request) {

    fileService.deleteFile(request);
    return BaseResponse.of(SuccessCode.SUCCESS, "success");
  }

}
