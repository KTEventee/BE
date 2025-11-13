package com.server.eventee.domain.comment.model;

import com.server.eventee.domain.Post.model.Post;
import com.server.eventee.domain.comment.dto.CommentRequest;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "comment")
@SQLDelete(sql = "UPDATE comment SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Comment(String content, Post post,Member member){
        this.content = content;
        this.post = post;
        this.member = member;
    }

    public void updateComment(CommentRequest.CommentUpdateDto dto){
        this.content = dto.contnet();
    }
}
