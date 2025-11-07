package com.server.eventee.domain.member.model;

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

    @Column(name = "profile_image_key", length = 255)
    private String profileImageKey;

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
