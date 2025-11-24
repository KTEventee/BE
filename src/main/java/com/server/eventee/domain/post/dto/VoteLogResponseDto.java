package com.server.eventee.domain.post.dto;

import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.post.model.VoteLog;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "투표 로그 응답 DTO")
public record VoteLogResponseDto(
        @Schema(description = "1번 옵션 투표 비율(%)", example = "62.5")
        double op1Percent,
        @Schema(description = "2번 옵션 투표 비율(%)", example = "37.5")
        double op2Percent,
        @Schema(description = "요청한 사용자 닉네임", example = "yongdev")
        String writer,
        @Schema(description = "요청한 사용자가 투표했는지 여부", example = "true")
        boolean isVote,
        @Schema(description = "요청한 사용자가 선택한 옵션 번호(1 또는 2)", example = "1")
        Integer voteNUm
) {
    public static VoteLogResponseDto from(List<VoteLog> logs,Member member){
        String writer = member.getNickname();
        long count1 = logs.stream()
                .filter(l -> l.getVoteNum() == 1)
                .count();

        long count2 = logs.stream()
                .filter(l -> l.getVoteNum() == 2)
                .count();

        long total = count1 + count2;

        double ratio1 = total > 0 ? (count1 * 100.0 / total) : 0;
        double ratio2 = total > 0 ? (count2 * 100.0 / total) : 0;
        Integer myVoteNum = logs.stream()
                    .filter(l -> l.getMember().getId().equals(member.getId()))
                    .map(VoteLog::getVoteNum)
                    .findFirst()
                    .orElse(0);

        return new VoteLogResponseDto(
                ratio1,
                ratio2,
                writer,
                myVoteNum!=0,
                myVoteNum
        );
    }
}
