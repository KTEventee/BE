package com.server.eventee.domain.post.dto;

import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.post.model.VoteLog;

import java.util.List;

public record VoteLogResponseDto(
        double op1Percent,
        double op2Percent,
        String writer,
        boolean isVote,
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
