package com.lpu.sessionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lpu.sessionservice.entity.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {
	boolean existsByUserIdAndMentorIdAndStatus(
		    Long userId, Long mentorId, String status);
}
