package com.logmonitoring.log_processor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class LogBroadcastService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public LogBroadcastService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 새로운 로그를 모든 연결된 클라이언트에게 브로드캐스트
     */
    public void broadcastNewLog(Map<String, Object> logData) {
        try {
            messagingTemplate.convertAndSend("/topic/logs", logData);
            log.info("📡 Broadcasted log to WebSocket clients: {}", logData.get("message"));
        } catch (Exception e) {
            log.error("❌ Failed to broadcast log: {}", e.getMessage(), e);
        }
    }

    /**
     * 연결된 클라이언트 수 등의 상태 정보 브로드캐스트
     */
    public void broadcastStats(Map<String, Object> stats) {
        try {
            messagingTemplate.convertAndSend("/topic/stats", stats);
        } catch (Exception e) {
            log.error("❌ Failed to broadcast stats: {}", e.getMessage(), e);
        }
    }
}
