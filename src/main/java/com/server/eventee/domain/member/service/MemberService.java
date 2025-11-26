package com.server.eventee.domain.member.service;

import com.server.eventee.domain.event.dto.MemberListDto;
import com.server.eventee.domain.member.dto.MemberMyPageResponse;
import com.server.eventee.domain.member.dto.MemberProfileImageDto.ConfirmUploadRequest;
import com.server.eventee.domain.member.dto.MemberProfileImageDto.DeleteImageResponse;
import com.server.eventee.domain.member.dto.MemberProfileImageDto.PresignedUrlResponse;
import com.server.eventee.domain.member.dto.MemberProfileImageDto.UploadIntentRequest;
import com.server.eventee.domain.member.model.Member;

import java.util.List;

public interface MemberService {

  String checkAndUpdateNickname(Member member, String nickname);
  MemberMyPageResponse getMyPageInfo(Member member);

}
