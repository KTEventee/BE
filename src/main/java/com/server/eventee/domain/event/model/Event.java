package com.server.eventee.domain.event.model;

import com.server.eventee.domain.event.model.MemberEvent;
import com.server.eventee.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "event")
@Builder
@SQLDelete(sql = "UPDATE event SET is_deleted = true, deleted_at = now() WHERE event_id = ?")
@SQLRestriction("is_deleted = false")
public class Event extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_id")
  private Long id;

  /** 이벤트 제목 */
  @NotNull
  @Column(name = "event_title", nullable = false, length = 100)
  private String title;

  /** 이벤트 설명 */
  @NotNull
  @Column(name = "event_description", nullable = false, columnDefinition = "TEXT")
  private String description;

  /** 초대 코드 (참가 시 사용) */
  @Column(name = "invite_code", length = 20, unique = true)
  private String inviteCode;

  /** 팀 수 또는 인원 제한 */
  @Column(name = "team_count")
  private Integer teamCount;

  /** 이벤트 비밀번호 (선택적) */
  @Column(name = "event_pass", length = 20)
  private String eventPass;

  /** 이벤트 시작 / 종료 시간 */
  @NotNull
  @Column(name = "start_at", nullable = false)
  private LocalDateTime startAt;

  @NotNull
  @Column(name = "end_at", nullable = false)
  private LocalDateTime endAt;

  /** 상태값 (예: OPEN, CLOSED 등) */
  @Column(name = "event_status", length = 20)
  private String status;

  /** 이벤트 썸네일 이미지 */
  @Column(name = "thumbnail_url", length = 512)
  private String thumbnailUrl;

  /** 참여자 목록 (MemberEvent 기준) */
  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MemberEvent> memberEvents = new ArrayList<>();

  /** 썸네일 갱신 */
  public void updateThumbnail(String url) {
    this.thumbnailUrl = url;
  }
}
