package com.logmonitoring.log_processor.consumer;

import com.logmonitoring.log_processor.dto.LogEntry;
import com.logmonitoring.log_processor.service.OpenSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogConsumer {
    private final OpenSearchService openSearchService;

    @Autowired
    public LogConsumer(OpenSearchService openSearchService) {
        this.openSearchService = openSearchService;
    }

    @KafkaListener(topics = "app-logs", groupId = "log-processors")
    public void consumeLog(ConsumerRecord<String, String> record) {
        try {
            String message = record.value();
            log.info("📨 Received message from Kafka: {}", message);

            // 간단한 LogEntry 생성 (나중에 JSON 파싱으로 발전)
            LogEntry logEntry = LogEntry.info(message, "web-app");

            // OpenSearch에 저장
            openSearchService.indexLog(logEntry);
        } catch (Exception e) {
            log.error("❌ Error processing log: {}", e.getMessage(), e);
        }
    }
}
