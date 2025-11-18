package com.server.eventee.domain.group.dto;

import com.server.eventee.domain.group.model.Group;

import java.util.List;

public class GroupResponse {

    public record ListDto(
            Group myGroup,
            List<Group> otherGroup
    ){}

}
