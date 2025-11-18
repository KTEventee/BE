package com.server.eventee.domain.member.service;

import com.server.eventee.domain.member.dto.MemberMyPageResponse;
import com.server.eventee.domain.member.dto.MemberProfileImageDto.ConfirmUploadRequest;
import com.server.eventee.domain.member.dto.MemberProfileImageDto.DeleteImageResponse;
import com.server.eventee.domain.member.dto.MemberProfileImageDto.PresignedUrlResponse;
import com.server.eventee.domain.member.dto.MemberProfileImageDto.UploadIntentRequest;
import com.server.eventee.domain.member.model.Member;

public interface MemberService {

  String checkAndUpdateNickname(Member member, String nickname);

  String confirmUpload(Member member, ConfirmUploadRequest request);

  DeleteImageResponse deleteProfileImage(Member member);

  PresignedUrlResponse createPresignedUrl(Member member, UploadIntentRequest request);
  MemberMyPageResponse getMyPageInfo(Member member);
}
