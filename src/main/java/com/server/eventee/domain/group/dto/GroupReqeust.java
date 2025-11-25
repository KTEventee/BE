package com.server.eventee.domain.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class GroupReqeust {

    @Schema(description = "그룹 생성 요청 DTO")
    public record GroupCreateDto(
            @Schema(description = "이벤트 ID", example = "7")
            long eventId,
            @Schema(description = "그룹 이름", example = "개발 1팀")
            String groupName,
            @Schema(description = "그룹 설명", example = "백엔드 개발을 담당하는 팀입니다.")
            String groupDescription
    ) {}

    @Schema(description = "그룹 이동 요청 DTO")
    public record GroupMoveDto(
            @Schema(description = "기존 그룹 ID", example = "3")
            long beforeGroupId,
            @Schema(description = "이동할 그룹 ID", example = "5")
            long afterGroupId,
            @Schema(description = "이동할 멤버 닉네임", example = "yongdev")
            String memberName
    ){
    }

    @Schema(description = "그룹 리더 변경 요청 DTO")
    public record GroupUpdateLeaderDto(
            @Schema(description = "그룹 ID", example = "4")
            Long groupId,
            @Schema(description = "새로운 리더 닉네임", example = "new_leader")
            String leader
    ) {
    }

    @Schema(description = "그룹 정보 수정 요청 DTO")
    public record GroupUpdateDto(

            @Schema(description = "그룹 ID", example = "4")
            Long groupId,
            @Schema(description = "그룹 이름", example = "디자인팀")
            String groupName,
            @Schema(description = "그룹 설명", example = "디자인 및 UI/UX를 담당합니다.")
            String groupDescription,
            @Schema(description = "그룹 이미지 URL", example = "https://eventee.s3.amazonaws.com/group/design.png")
            String imgUrl,
            @Schema(description = "그룹 리더 닉네임", example = "design_master")
            String leader
    ) {}
}
