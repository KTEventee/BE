package com.server.eventee.domain.group.service;

import com.server.eventee.domain.event.excepiton.EventHandler;
import com.server.eventee.domain.event.excepiton.status.EventErrorStatus;
import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.event.repository.EventRepository;
import com.server.eventee.domain.group.dto.*;
import com.server.eventee.domain.group.model.Group;
import com.server.eventee.domain.group.model.MemberGroup;
import com.server.eventee.domain.group.repository.GroupRepository;
import com.server.eventee.domain.group.repository.MemberGroupRepository;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class GroupServiceImpl implements GroupService{

    private final GroupRepository groupRepository;
    private final EventRepository eventRepository;
    private final MemberGroupRepository memberGroupRepository;

    @Transactional
    public void createAdditionalGroup(GroupReqeust.GroupCreateDto request
            ,Member member
    ){

        Event event = eventRepository.findByIdAndIsDeletedFalse(request.eventId()).orElseThrow(
                () -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND)
        );

        int tmpGroupCnt=event.getGroups().size();
        String leaderName = "test";

        int nextNo = tmpGroupCnt+1;

        Group group = buildGroup(request, leaderName, nextNo);
        Group saved = groupRepository.save(group);

        MemberGroup memberGroup = MemberGroup.builder()
                .member(member)
                .group(saved)
                .build();

        memberGroupRepository.save(memberGroup);
        log.info("Group save complete");
    }

    @Transactional
    public void updateGroup(GroupReqeust.GroupUpdateDto request){
        Group group = loadGroupById(request.groupId());
        if(group.updateGroup(request)) groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(Long id){
        Group group = loadGroupById(id);
        groupRepository.delete(group);
    }

    @Transactional
    public void updateLeader(GroupReqeust.GroupUpdateLeaderDto request){
        //note 리더 변경없는지 있는지 에러 날릴지 고민중.
        Group group = loadGroupById(request.groupId());
        if(group.updateLeader(request)) groupRepository.save(group);
    }

    @Transactional(readOnly = true)
    public GroupResponse.ListDto getGroupByEvent(Long eventId,Member member){

        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId).orElseThrow(
                () -> new BaseException(ErrorCode.EVENT_NOT_FOUND)
        );

        List<Group> groups = event.getGroups();

        Group myGroup = null;
        List<Group> otherGroups = new ArrayList<>();

        for(Group g : groups){
//            if(isJoin(g,member)) myGroup = g;
//            else otherGroups.add(g);
            otherGroups.add(g);
        }

        return GroupResponse.ListDto.from(myGroup, otherGroups);
    }

    @Transactional
    public void enterGroup(Long id,Member member){
        Group group = loadGroupById(id);

        group.addMember(member);
        groupRepository.save(group);
    }

    @Transactional
    public void leaveGroup(Long id,Member member){
        Group group = loadGroupById(id);

        group.leaveMember(member);
        groupRepository.save(group);
    }

    @Transactional
    public void moveGroup(GroupReqeust.GroupMoveDto request,Member member){
        Group beforeGroup = loadGroupById(request.beforeGroupId());
        Group afterGroup = loadGroupById(request.afterGroupId());

        beforeGroup.leaveMember(member);
        afterGroup.addMember(member);

        groupRepository.save(beforeGroup);
        groupRepository.save(afterGroup);
    }

    private Boolean isJoin(Group g, Member m){
        List<MemberGroup> memberGroups = memberGroupRepository.findMemberGroupsByGroup(g);
        for(MemberGroup mg : memberGroups){
            if(mg.getMember().getId().equals(m.getId())) return true;
        }
        return false;
    }


    private Group loadGroupById(Long groupUd){
        return groupRepository.findGroupByGroupId(groupUd).orElseThrow(
                () -> new BaseException(ErrorCode.GROUP_NOT_FOUND)
        );
    }

    private Group buildGroup(GroupReqeust.GroupCreateDto req, String leaderName, int groupNo) {
        return Group.builder()
                .groupName(req.groupName())
                .groupDescription(req.groupDescription())
                .groupImg(req.imgUrl())
                .groupLeader(leaderName)
                .groupNo(groupNo)
                .build();
    }
}
