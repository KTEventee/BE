package com.server.eventee.domain.group.dto;

public record GroupCreateRequestDto(
    String groupName,
    String groupDescription,
    String imgUrl
) {}
