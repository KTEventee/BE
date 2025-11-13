package com.server.eventee.domain.post.dto;

import com.server.eventee.domain.post.model.VoteLog;

import java.util.List;

public record VoteLogResponseDto(
        String writer,
        String content
) {
    public static VoteLogResponseDto from(VoteLog log){
        //fixme memeber 수정
        return new VoteLogResponseDto(
                "test",
                log.getVoteWord()
        );
    }

    public static List<VoteLogResponseDto> from(List<VoteLog> logs){
        return logs.stream().map(VoteLogResponseDto::from).toList();
    }

}
