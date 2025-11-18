package com.server.eventee.domain.member.model;

import com.server.eventee.domain.group.model.MemberGroup;
import com.server.eventee.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "member")
@Builder
@SQLDelete(sql = "UPDATE member SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "social_id", nullable = false, length = 255)
    private String socialId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @NotNull
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @NotNull
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @OneToMany
    private List<MemberGroup> memberGroups = new ArrayList<>();

    /**
     * S3 객체 key (예: profiles/1/uuid.jpg)
     * 이미지 파일의 실제 경로(키) 저장용
     */
    @Column(name = "profile_image_key", length = 255)
    private String profileImageKey;

    /**
     * CloudFront 또는 S3 Public URL
     * 프론트엔드에서 실제 접근 가능한 URL
     */
    @Column(name = "profile_image_url", length = 512)
    private String profileImageUrl;

    /** 닉네임 변경 */
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    /** 프로필 이미지 정보 변경 */
    public void updateProfileImage(String key, String url) {
        this.profileImageKey = key;
        this.profileImageUrl = url;
    }

    /** 프로필 이미지 삭제 (DB에서만 제거) */
    public void clearProfileImage() {
        this.profileImageKey = null;
        this.profileImageUrl = null;
    }


    public enum Role {
        USER("USER"),
        ADMIN("ADMIN");

        private final String value;

        Role(String value) {
            this.value = value;
        }

        public boolean isAdmin() {
            return this == ADMIN;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
