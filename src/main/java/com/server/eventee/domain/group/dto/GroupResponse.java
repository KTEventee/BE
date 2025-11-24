package com.server.eventee.domain.group.dto;

import com.server.eventee.domain.group.model.Group;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class GroupResponse {

    @Schema(description = "그룹 목록 응답 DTO(내 그룹 + 다른 그룹들)")
    public record ListDto(
            @Schema(description = "내 그룹 정보(null일 수 있음)")
            GroupDto myGroup,
            @Schema(description = "다른 그룹 리스트")
            List<GroupDto> otherGroup
    ){
        public static ListDto from(Group my, List<Group> others){
            return new ListDto(
                    GroupDto.from(my),
                    GroupDto.from(others)
            );
        }
    }

    @Schema(description = "그룹 정보 DTO")
    public record GroupDto(
            @Schema(description = "그룹 ID", example = "4")
            long groupId,
            @Schema(description = "그룹 이름", example = "백엔드팀")
            String groupName,
            @Schema(description = "그룹 설명", example = "서버 개발을 담당하는 팀입니다.")
            String groupDescription,
            @Schema(description = "그룹 이미지 URL", example = "https://eventee.s3.amazonaws.com/group/backend.png")
            String groupImg,
            @Schema(description = "그룹 번호(정렬/순서를 위한 값)", example = "1")
            int groupNo,
            @Schema(description = "그룹 리더 닉네임(초기 null 가능)", example = "leader_kim")
            String groupLeader
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
