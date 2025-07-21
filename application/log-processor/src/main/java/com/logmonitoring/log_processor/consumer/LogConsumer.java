package com.logmonitoring.log_processor.consumer;

import com.logmonitoring.log_processor.dto.LogEntry;
import com.logmonitoring.log_processor.service.LogBroadcastService;
import com.logmonitoring.log_processor.service.OpenSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LogConsumer {
    private final OpenSearchService openSearchService;
    private final LogBroadcastService broadcastService;

    @Autowired
    public LogConsumer(OpenSearchService openSearchService, LogBroadcastService broadcastService) {
        this.openSearchService = openSearchService;
        this.broadcastService = broadcastService;
    }

    @KafkaListener(topics = "app-logs", groupId = "log-processors")
    public void consumeLog(ConsumerRecord<String, String> record) {
        try {
            String message = record.value();
            log.info("📨 Received message from Kafka: {}", message);

            LogEntry logEntry = LogEntry.info(message, "web-app");

            openSearchService.indexLog(logEntry);

            Map<String, Object> logData = new HashMap<>();
            logData.put("id", logEntry.id());
            logData.put("message", logEntry.message());
            logData.put("level", logEntry.level());
            logData.put("service", logEntry.service());
            logData.put("timestamp", logEntry.timestamp().toString());

            broadcastService.broadcastNewLog(logData);
        } catch (Exception e) {
            log.error("❌ Error processing log: {}", e.getMessage(), e);
        }
    }
}
