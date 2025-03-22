package com.example.youtube.application.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.youtube.application.entity.EventLog;
import com.example.youtube.application.repository.EventLogRepository;

@Service
public class EventLogService {
    @Autowired
    private EventLogRepository eventLogRepository;

    public void logEvent(String action) {
        EventLog log = new EventLog();
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());
        eventLogRepository.save(log);
    }
}

