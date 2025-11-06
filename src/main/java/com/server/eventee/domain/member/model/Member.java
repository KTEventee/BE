package com.server.eventee.domain.member.model;

import com.server.eventee.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "member")
@Builder
@SQLDelete(sql = "UPDATE member SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Member extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull private String memberName;     //실명
    @NotNull private String email;          //이메일
    @NotNull private String password;
    @NotNull private LocalDate birth;
    @NotNull private Role role;


    public enum Role {
        USER("USER"),
        ADMIN("ADMIN");

        private final String role;

        Role(String role) {
            this.role = role;
        }

        public boolean isAdmin(){
            return this.equals(Role.ADMIN);
        }
    }


}
