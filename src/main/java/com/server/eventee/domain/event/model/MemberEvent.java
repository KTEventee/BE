package com.server.eventee.domain.event.model;

import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "member_event",
    uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "event_id"}))
@Builder
@SQLDelete(sql = "UPDATE member_event SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class MemberEvent extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 회원 FK */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  /** 이벤트 FK */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;

  /** 역할: HOST / PARTICIPANT */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 20)
  private MemberEventRole role;

  public enum MemberEventRole {
    HOST,
    PARTICIPANT
  }
}
