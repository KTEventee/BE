package com.server.eventee.domain.group.service;

import com.server.eventee.domain.group.dto.*;
import com.server.eventee.domain.group.model.Group;
import com.server.eventee.domain.group.repository.GroupRepository;
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

    @Transactional
    public void createAdditionalGroup(GroupReqeust.GroupCreateDto request){

        //fixme 이벤트에 현재 그룹 몇개 있는지 알아야함
        int tmpGroupCnt=0;
        String leaderName = "test";

        int nextNo = tmpGroupCnt+1;

        Group group = buildGroup(request, leaderName, nextNo);
        Group saved = groupRepository.save(group);
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
    public GroupResponse.ListDto getGroupByEvent(Long eventId){
        //fixme event가져오기 , member가져오기
        Member member = null;


        // note event에서 가져온 groups
        List<Group> groups = new ArrayList<>();

        Group myGroup = null;
        List<Group> otherGroups = new ArrayList<>();

        for(Group g : groups){
            if(isJoin(g,member)) myGroup = g;
            else otherGroups.add(g);
        }

        return new GroupResponse.ListDto(myGroup,otherGroups);

    }

    @Transactional
    public void enterGroup(Long id){
        Member member = null;
        Group group = loadGroupById(id);

        group.addMember(member);
        groupRepository.save(group);
    }

    @Transactional
    public void leaveGroup(Long id){
        Member member = null;
        Group group = loadGroupById(id);

        group.leaveMember(member);
        groupRepository.save(group);
    }

    @Transactional
    public void moveGroup(GroupReqeust.GroupMoveDto request){
        Member member = null;

        Group beforeGroup = loadGroupById(request.beforeGroupId());
        Group afterGroup = loadGroupById(request.afterGroupId());

        beforeGroup.leaveMember(member);
        afterGroup.addMember(member);

        groupRepository.save(beforeGroup);
        groupRepository.save(afterGroup);
    }

    private Boolean isJoin(Group g, Member m){
        //fixme 그룹에 속해 있는지 판단하는 메소드 로직 수정해야함
        return true;
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
