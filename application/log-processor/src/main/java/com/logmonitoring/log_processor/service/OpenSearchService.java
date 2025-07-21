package com.logmonitoring.log_processor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logmonitoring.log_processor.dto.LogEntry;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class OpenSearchService {
    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    @Autowired
    public OpenSearchService(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public void indexLog(LogEntry logEntry) {
        try {
            // 인덱스 이름을 날짜별로 생성 (logs-2025.07.21)
            String indexName = "logs-" + logEntry.timestamp().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

            // LogEntry를 JSON으로 변환
            String jsonString = objectMapper.writeValueAsString(logEntry);

            // OpenSearch에 문서 인덱싱
            IndexRequest request = new IndexRequest(indexName)
                    .id(logEntry.id())
                    .source(jsonString, XContentType.JSON);

            IndexResponse response = client.index(request, RequestOptions.DEFAULT);

            log.info("✅ Indexed log to OpenSearch: {} in index: {}", response.getId(), indexName);
        } catch (Exception e) {
            log.error("❌ Failed to index log to OpenSearch: {}", e.getMessage(), e);
        }
    }
}