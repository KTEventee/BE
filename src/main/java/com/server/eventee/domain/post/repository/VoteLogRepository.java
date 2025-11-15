package com.server.eventee.domain.post.repository;

import com.server.eventee.domain.post.model.VoteLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteLogRepository extends JpaRepository<VoteLog,Long> {
}
