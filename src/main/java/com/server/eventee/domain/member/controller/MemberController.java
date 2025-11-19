package com.server.eventee.domain.member.controller;

import com.server.eventee.domain.member.dto.MemberMyPageResponse;
import com.server.eventee.domain.member.dto.MemberProfileImageDto;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.member.service.MemberService;
import com.server.eventee.global.exception.BaseResponse;
import com.server.eventee.global.exception.codes.SuccessCode;
import com.server.eventee.global.filter.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원 관리 및 마이페이지 관련 API")
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

  private final MemberService memberService;

  // 닉네임 중복 확인 및 변경
  @Operation(
      summary = "닉네임 중복 확인 및 변경",
      description = """
          사용자가 입력한 닉네임의 중복 여부를 확인하고,
          중복이 없으면 해당 닉네임으로 회원 정보를 업데이트합니다.
          """
  )
  @PatchMapping(value = "/nickname", produces = "application/json")
  public BaseResponse<String> checkAndUpdateNickname(
      @CurrentMember Member member,
      @RequestParam("nickname") @NotBlank String nickname) {

    String updatedNickname = memberService.checkAndUpdateNickname(member, nickname);
    return BaseResponse.of(SuccessCode.SUCCESS, updatedNickname);
  }

  //Presigned URL (PUT) 발급
  @Operation(
      summary = "프로필 이미지 Presigned URL 발급 (PUT)",
      description = """
          S3에 직접 PUT 업로드할 URL을 발급합니다.
          프론트는 해당 URL로 이미지를 업로드한 뒤 /confirm을 호출해야 합니다.
          """
  )
  @PostMapping("/profile-image/presigned-url")
  public BaseResponse<MemberProfileImageDto.PresignedUrlResponse> createPresignedUrl(
      @CurrentMember Member member,
      @Valid @RequestBody MemberProfileImageDto.UploadIntentRequest request) {

    MemberProfileImageDto.PresignedUrlResponse response =
        memberService.createPresignedUrl(member, request);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  //업로드 확정 (PUT 완료 후 호출)
  @Operation(
      summary = "프로필 이미지 업로드 확정",
      description = "PUT 업로드가 완료된 이미지를 확인하고 회원 프로필에 반영합니다."
  )
  @PostMapping("/profile-image/confirm")
  public BaseResponse<String> confirmProfileImage(
      @CurrentMember Member member,
      @Valid @RequestBody MemberProfileImageDto.ConfirmUploadRequest request) {

    String imageUrl = memberService.confirmUpload(member, request);
    return BaseResponse.of(SuccessCode.SUCCESS, imageUrl);
  }

  //프로필 이미지 삭제
  @Operation(
      summary = "프로필 이미지 삭제",
      description = "S3에서 기존 프로필 이미지를 삭제하고 회원 프로필을 초기화합니다."
  )
  @DeleteMapping("/profile-image")
  public BaseResponse<MemberProfileImageDto.DeleteImageResponse> deleteProfileImage(
      @CurrentMember Member member) {

    MemberProfileImageDto.DeleteImageResponse response =
        memberService.deleteProfileImage(member);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }

  //마이페이지
  @Operation(
      summary = "마이페이지 정보 조회",
      description = """
        로그인한 회원의 닉네임, 프로필 이미지, 
        참여한 이벤트 목록을 반환합니다.
        """
  )
  @GetMapping("/mypage")
  public BaseResponse<MemberMyPageResponse> getMyPageInfo(@CurrentMember Member member) {

    MemberMyPageResponse response = memberService.getMyPageInfo(member);
    return BaseResponse.of(SuccessCode.SUCCESS, response);
  }
}
