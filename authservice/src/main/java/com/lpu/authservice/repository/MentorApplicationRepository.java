package com.lpu.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lpu.authservice.entity.MentorApplication;

public interface MentorApplicationRepository extends JpaRepository<MentorApplication, Long> {
}
