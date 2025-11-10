package com.server.eventee.domain.group.model;

import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.group.dto.GroupReqeust;
import com.server.eventee.global.entity.BaseEntity;
import com.server.eventee.domain.member.model.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "event_group")
@SQLDelete(sql = "UPDATE event_group SET is_deleted = true, deleted_at = now() where group_id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @NotNull private String groupName;
    @NotNull private String groupDescription;
    @NotNull private String groupImg;
    @NotNull private int groupNo;
    @NotNull private String groupLeader;

    // member 추가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Builder(toBuilder = true)
    private Group(
        Long groupId,
        @NotNull String groupName,
        @NotNull String groupDescription,
        @NotNull String groupImg,
        @NotNull int groupNo,
        @NotNull String groupLeader,
        @NotNull Event event
    ) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupImg = groupImg;
        this.groupNo = groupNo;
        this.groupLeader = groupLeader;
        this.event = event;
    }

    public boolean updateLeader(GroupReqeust.GroupUpdateLeaderDto dto){
        if (dto.leader() != null) {
            String nv = dto.leader().trim();
            if (!Objects.equals(this.groupLeader, nv)) {
                this.groupLeader = nv;
                return true;
            }
        }
        return false;
    }

    public boolean updateGroup(GroupReqeust.GroupUpdateDto dto){
        boolean changed = false;

        if (dto.groupName() != null) {
            String nv = dto.groupName().trim();
            if (!Objects.equals(this.groupName, nv)) {
                this.groupName = nv;
                changed = true;
            }
        }

        if (dto.groupDescription() != null) {
            String nv = dto.groupDescription().trim();
            if (!Objects.equals(this.groupDescription, nv)) {
                this.groupDescription = nv;
                changed = true;
            }
        }

        if (dto.imgUrl() != null) {
            String nv = dto.imgUrl().trim();
            if (!Objects.equals(this.groupImg, nv)) {
                this.groupImg = nv;
                changed = true;
            }
        }

        return changed;
    }

    public void addMember(Member member){
        //fixme member 관계 설정 후에 수정해야함
    }

    public void leaveMember(Member member){

    }

}
