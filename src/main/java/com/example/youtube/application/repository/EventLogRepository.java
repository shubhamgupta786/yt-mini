package com.example.youtube.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.youtube.application.entity.EventLog;

@Repository
public interface EventLogRepository extends JpaRepository<EventLog, Long> {}