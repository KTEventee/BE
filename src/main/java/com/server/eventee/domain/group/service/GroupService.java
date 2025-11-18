package com.server.eventee.domain.group.service;

import com.server.eventee.domain.group.dto.*;
import com.server.eventee.domain.member.model.Member;

public interface GroupService {
    void createAdditionalGroup(GroupReqeust.GroupCreateDto request,Member member);
    void deleteGroup(Long id);
    void updateGroup(GroupReqeust.GroupUpdateDto request);
    void updateLeader(GroupReqeust.GroupUpdateLeaderDto request);
    GroupResponse.ListDto getGroupByEvent(Long eventId,Member member);
    void enterGroup(Long id, Member member);
    void leaveGroup(Long id,Member member);
    void moveGroup(GroupReqeust.GroupMoveDto request,Member member);
}
