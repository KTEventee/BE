package com.server.eventee.domain.group.dto;

import com.server.eventee.domain.group.model.Group;

import java.util.List;

public class GroupResponse {

    public record ListDto(
            GroupDto myGroup,
            List<GroupDto> otherGroup
    ){
        public static ListDto from(Group my, List<Group> others){
            return new ListDto(
                    GroupDto.from(my),
                    GroupDto.from(others)
            );
        }
    }

    public record GroupDto(
            long groupId,
            String groupName,
            String groupDescription,
            String groupImg,
            int groupNo, //그룹 이름 바뀔경우 순서를 위함.
            String groupLeader //초기에는 Null값임
    ){
        public static GroupDto from(Group group){
            if(group == null) return null;
            return new GroupDto(
                    group.getGroupId(),
                    group.getGroupName(),
                    group.getGroupDescription(),
                    group.getGroupImg(),
                    group.getGroupNo(),
                    group.getGroupLeader()
            );
        }

        public static  List<GroupDto> from(List<Group> groups){
            return groups.stream().map(
                    GroupDto::from
            ).toList();
        }
    }

}
