package com.server.eventee.domain.event.dto;

import com.server.eventee.domain.member.model.Member;

import java.util.List;

public class MemberListDto {
    public record MemberDtoByGroup(){
        //todo 추후에 조별로 입장을 하면 만들어야함.
    }

    public record MemberDto(
            long id,
            String email,
            String nickname,
            String profileImageUrl,
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
