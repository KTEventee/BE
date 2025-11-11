package com.server.eventee.domain.event.model;

import com.server.eventee.domain.group.model.Group;
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

  @NotNull
  @Column(name = "event_title", nullable = false, length = 100)
  private String title;

  @NotNull
  @Column(name = "event_description", nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "invite_code", length = 20, unique = true)
  private String inviteCode;

  @Column(name = "team_count")
  private Integer teamCount;

  // 이벤트 비밀번호 (입장 시 검증용)
  @NotNull
  @Column(name = "event_password", nullable = false, length = 100)
  private String password;

  @NotNull
  @Column(name = "start_at", nullable = false)
  private LocalDateTime startAt;

  @NotNull
  @Column(name = "end_at", nullable = false)
  private LocalDateTime endAt;

  @Column(name = "event_status", length = 20)
  private String status;

  @Column(name = "thumbnail_url", length = 512)
  private String thumbnailUrl;

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MemberEvent> memberEvents = new ArrayList<>();

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Group> groups = new ArrayList<>();

  public void updateThumbnail(String url) {
    this.thumbnailUrl = url;
  }
}
