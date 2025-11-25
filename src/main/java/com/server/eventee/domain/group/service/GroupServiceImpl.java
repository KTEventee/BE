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
    public void createAdditionalGroup(GroupReqeust.GroupCreateDto request, Member member) {

        // 1) eventId로 이벤트 찾기
        Event event = eventRepository.findByIdAndIsDeletedFalse(request.eventId())
            .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

        // 2) 그룹 번호(nextNo) 계산
        int nextNo = event.getGroups().size() + 1;


        // 3) Group 객체 생성
        Group group = Group.builder()
            .event(event)
            .groupName(request.groupName())
            .groupDescription(request.groupDescription())
            .groupImg(null)
            .groupNo(nextNo)
            .build();

        // 4) 저장
        Group saved = groupRepository.save(group);

        // 5) 생성자를 해당 그룹의 멤버로 배정
        MemberGroup memberGroup = MemberGroup.builder()
            .member(member)
            .group(saved)
            .build();

        memberGroupRepository.save(memberGroup);

        log.info("Group successfully created with eventId = {}", event.getId());
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
}
