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
