package com.server.eventee.domain.Post.model;

import com.server.eventee.domain.Post.dto.PostRequest;
import com.server.eventee.domain.comment.model.Comment;
import com.server.eventee.domain.group.model.Group;
import com.server.eventee.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "post")
@SQLDelete(sql = "UPDATE post SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

//    private Member wirter;
    private String content;
    private PostType postType;

    @ManyToOne
    private Group group;

    @OneToMany
    private List<VoteLog> voteLogs = new ArrayList<>();

    @OneToMany
    private List<Comment> comments = new ArrayList<>();;

    public void addComment(Comment c){
        if(comments.contains(c)) return;
        comments.add(c);
    }

    public void deleteComment(Comment c){
        if(!comments.contains(c)) return;
        comments.remove(c);
    }

    public void addVoteLog(VoteLog log){
        if(voteLogs.contains(log)) return;
        voteLogs.add(log);
    }

    public void deleteLog(VoteLog log){
        if(!voteLogs.contains(log)) return;
        voteLogs.remove(log);
    }

    @Builder
    public Post(String content, PostType type,Group group){
        this.content = content;
        this.postType = type;
        this.group = group;
    }

    public void updatePost(PostRequest.PostDto dto){
        this.content = content;
        this.postType = PostType.from(dto.type());
    }


}
