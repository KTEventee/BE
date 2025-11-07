package com.server.eventee.domain.member.controller;

import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.member.service.MemberService;
import com.server.eventee.global.exception.BaseResponse;
import com.server.eventee.global.exception.codes.SuccessCode;
import com.server.eventee.global.filter.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원 관리 및 마이페이지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

  private final MemberService memberService;

  @Operation(
      summary = "닉네임 중복 확인",
      description = """
                    사용자가 입력한 닉네임이 이미 존재하는지 확인합니다.
                    """)
  @Parameters({
      @Parameter(
          name = "nickname",
          description = "중복 확인을 요청할 닉네임",
          required = true,
          example = "testUser")
  })
  @GetMapping(value = "/check-nickname", produces = "application/json")
  public BaseResponse<Boolean> confirmNickName(
      @CurrentMember Member member,
      @RequestParam("nickname") @NotBlank String nickname) {

    boolean isAvailable = memberService.confirmNickName(member, nickname);
    return BaseResponse.of(SuccessCode.SUCCESS, isAvailable);
  }
}
