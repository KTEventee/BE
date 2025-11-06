package com.server.eventee.domain.group.service;

import com.server.eventee.domain.group.dto.*;

public interface GroupService {
    void createAdditionalGroup(GroupReqeust.GroupCreateDto request);
    void deleteGroup(Long id);
    void updateGroup(GroupReqeust.GroupUpdateDto request);
    void updateLeader(GroupReqeust.GroupUpdateLeaderDto request);
    GroupResponse.ListDto getGroupByEvent(Long eventId);
    void enterGroup(Long id);
    void leaveGroup(Long id);
    void moveGroup(GroupReqeust.GroupMoveDto request);
}
