package com.server.eventee.domain.group.dto;

public record GroupUpdateRequest(
        Long groupId,
        String groupName,
        String groupDescription,
        String imgUrl
){
}
