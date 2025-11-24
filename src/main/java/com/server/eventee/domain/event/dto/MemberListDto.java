package com.server.eventee.domain.event.dto;

import com.server.eventee.domain.member.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class MemberListDto {
    public record MemberDtoByGroup(){
        //todo 추후에 조별로 입장을 하면 만들어야함.
    }

    @Schema(description = "멤버 정보 DTO")
    public record MemberDto(
            @Schema(description = "멤버 ID", example = "10")
            long id,
            @Schema(description = "이메일", example = "test@test.com")
            String email,
            @Schema(description = "닉네임", example = "yongdev")
            String nickname,
            @Schema(description = "프로필 이미지 URL", example = "https://eventee.s3.amazonaws.com/profile/10.png")
            String profileImageUrl,
            @Schema(description = "역할(USER/ADMIN 등)", example = "USER")
            String role
    ){
        public static MemberDto from(Member member){
            return new MemberDto(
                    member.getId(),
                    member.getEmail(),
                    member.getNickname(),
                    member.getProfileImageUrl(),
                    member.getRole().toString()
            );
        }

        public static List<MemberDto> from(List<Member> members){
            return members.stream().map(
                    MemberDto::from
            ).toList();
        }
    }
}
