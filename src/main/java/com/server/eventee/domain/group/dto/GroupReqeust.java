package com.server.eventee.domain.group.dto;

public class GroupReqeust {

    public record GroupCreateDto(
            long eventId,
            String groupName,
            String groupDescription,
            String imgUrl
    ) {}

    public record GroupMoveDto(
            long beforeGroupId,
            long afterGroupId,
            String memberName
    ){
    }

    public record GroupUpdateLeaderDto(
            Long groupId,
            String leader
    ) {
    }

    public record GroupUpdateDto(
            Long groupId,
            String groupName,
            String groupDescription,
            String imgUrl,
            String leader
    ){
    }
}
