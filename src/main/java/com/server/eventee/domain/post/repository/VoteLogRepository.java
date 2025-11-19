package com.server.eventee.domain.post.repository;

import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.post.model.Post;
import com.server.eventee.domain.post.model.VoteLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteLogRepository extends JpaRepository<VoteLog,Long> {
    Optional<VoteLog> findVoteLogByMemberAndPost(Member member, Post post);
}
