package com.server.eventee.domain.Post.repository;

import com.server.eventee.domain.Post.model.VoteLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteLogRepository extends JpaRepository<VoteLog,Long> {
}
