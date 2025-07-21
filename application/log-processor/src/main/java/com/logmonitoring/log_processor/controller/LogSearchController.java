package com.logmonitoring.log_processor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class LogSearchController {
    private final RestHighLevelClient openSearchClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public LogSearchController(RestHighLevelClient openSearchClient, ObjectMapper objectMapper) {
        this.openSearchClient = openSearchClient;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/search")
    public Map<String, Object> searchLogs(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // 검색 쿼리 설정
            if (query.isEmpty()) {
                sourceBuilder.query(QueryBuilders.matchAllQuery());
            } else {
                sourceBuilder.query(QueryBuilders.multiMatchQuery(query, "message", "service", "level"));
            }

            // 페이징 및 정렬
            sourceBuilder.from(page * size);
            sourceBuilder.size(size);
            sourceBuilder.sort("timestamp", SortOrder.DESC);

            // 검색 실행
            SearchRequest searchRequest = new SearchRequest("logs-*");
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = openSearchClient.search(searchRequest, RequestOptions.DEFAULT);

            // 결과 변환
            List<Map<String, Object>> logs = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits()) {
                Map<String, Object> log = hit.getSourceAsMap();
                log.put("id", hit.getId());
                logs.add(log);
            }

            // 응답 구성
            Map<String, Object> response = new HashMap<>();
            response.put("logs", logs);
            response.put("total", searchResponse.getHits().getTotalHits().value());
            response.put("page", page);
            response.put("size", size);

            return response;

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "검색 중 오류 발생: " + e.getMessage());
            return errorResponse;
        }
    }

    @GetMapping("/recent")
    public List<Map<String, Object>> getRecentLogs(@RequestParam(defaultValue = "10") int limit) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            sourceBuilder.sort("timestamp", SortOrder.DESC);
            sourceBuilder.size(limit);

            SearchRequest searchRequest = new SearchRequest("logs-*");
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = openSearchClient.search(searchRequest, RequestOptions.DEFAULT);

            List<Map<String, Object>> logs = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits()) {
                Map<String, Object> log = hit.getSourceAsMap();
                log.put("id", hit.getId());
                logs.add(log);
            }

            return logs;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @GetMapping("/stats")
    public Map<String, Object> getLogStats() {
        try {
            // 간단한 통계 (총 로그 수, 최근 1시간 로그 수 등)
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            sourceBuilder.size(0); // 카운트만 필요

            SearchRequest searchRequest = new SearchRequest("logs-*");
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = openSearchClient.search(searchRequest, RequestOptions.DEFAULT);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalLogs", searchResponse.getHits().getTotalHits().value());
            stats.put("timestamp", System.currentTimeMillis());

            return stats;

        } catch (Exception e) {
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", e.getMessage());
            return errorStats;
        }
    }
}
