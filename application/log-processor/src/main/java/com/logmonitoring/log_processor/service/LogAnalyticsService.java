package com.logmonitoring.log_processor.service;

import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.RangeQueryBuilder;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.opensearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.opensearch.search.aggregations.bucket.histogram.Histogram;
import org.opensearch.search.aggregations.bucket.terms.Terms;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LogAnalyticsService {
    private final RestHighLevelClient openSearchClient;

    @Autowired
    public LogAnalyticsService(RestHighLevelClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    /**
     * 로그 레벨별 통계
     */
    public Map<String, Object> getLogLevelStats(int hours) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // 최근 N시간 데이터만 조회
            LocalDateTime since = LocalDateTime.now().minusHours(hours);
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("timestamp")
                    .gte(since.toString());

            sourceBuilder.query(rangeQuery);
            sourceBuilder.size(0); // 집계만 필요

            // 로그 레벨별 집계
            sourceBuilder.aggregation(
                    AggregationBuilders.terms("levels")
                            .field("level.keyword")
                            .size(10)
            );

            SearchRequest searchRequest = new SearchRequest("logs-*");
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = openSearchClient.search(searchRequest, RequestOptions.DEFAULT);

            Map<String, Object> result = new HashMap<>();
            Terms levels = searchResponse.getAggregations().get("levels");

            Map<String, Long> levelCounts = new HashMap<>();
            long total = 0;

            for (Terms.Bucket bucket : levels.getBuckets()) {
                String level = bucket.getKeyAsString();
                long count = bucket.getDocCount();
                levelCounts.put(level, count);
                total += count;
            }

            result.put("levels", levelCounts);
            result.put("total", total);
            result.put("period", hours + "시간");

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", e.getMessage());
            return errorResult;
        }
    }

    /**
     * 시간대별 로그 수 통계 (시계열)
     */
    public Map<String, Object> getTimeSeriesStats(int hours) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            LocalDateTime since = LocalDateTime.now().minusHours(hours);
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("timestamp")
                    .gte(since.toString());

            sourceBuilder.query(rangeQuery);
            sourceBuilder.size(0);

            // 시간대별 집계 (10분 간격)
            sourceBuilder.aggregation(
                    AggregationBuilders.dateHistogram("timeline")
                            .field("timestamp")
                            .calendarInterval(DateHistogramInterval.minutes(10))
                            .minDocCount(0)
            );

            SearchRequest searchRequest = new SearchRequest("logs-*");
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = openSearchClient.search(searchRequest, RequestOptions.DEFAULT);

            Map<String, Object> result = new HashMap<>();
            Histogram timeline = searchResponse.getAggregations().get("timeline");

            List<Map<String, Object>> timeData = new ArrayList<>();

            for (Histogram.Bucket bucket : timeline.getBuckets()) {
                Map<String, Object> point = new HashMap<>();
                point.put("time", bucket.getKeyAsString());
                point.put("count", bucket.getDocCount());
                timeData.add(point);
            }

            result.put("timeline", timeData);
            result.put("period", hours + "시간");

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", e.getMessage());
            return errorResult;
        }
    }

    /**
     * 서비스별 통계
     */
    public Map<String, Object> getServiceStats(int hours) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            LocalDateTime since = LocalDateTime.now().minusHours(hours);
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("timestamp")
                    .gte(since.toString());

            sourceBuilder.query(rangeQuery);
            sourceBuilder.size(0);

            // 서비스별 집계
            sourceBuilder.aggregation(
                    AggregationBuilders.terms("services")
                            .field("service.keyword")
                            .size(10)
            );

            SearchRequest searchRequest = new SearchRequest("logs-*");
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = openSearchClient.search(searchRequest, RequestOptions.DEFAULT);

            Map<String, Object> result = new HashMap<>();
            Terms services = searchResponse.getAggregations().get("services");

            Map<String, Long> serviceCounts = new HashMap<>();

            for (Terms.Bucket bucket : services.getBuckets()) {
                String service = bucket.getKeyAsString();
                long count = bucket.getDocCount();
                serviceCounts.put(service, count);
            }

            result.put("services", serviceCounts);
            result.put("period", hours + "시간");

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", e.getMessage());
            return errorResult;
        }
    }

    /**
     * 대시보드 요약 통계
     */
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        // 최근 1시간 통계
        Map<String, Object> recent1h = getLogLevelStats(1);
        summary.put("recent1h", recent1h);

        // 최근 24시간 통계
        Map<String, Object> recent24h = getLogLevelStats(24);
        summary.put("recent24h", recent24h);

        // 시계열 데이터 (최근 6시간, 30분 간격)
        Map<String, Object> timeSeries = getTimeSeriesStats(6);
        summary.put("timeSeries", timeSeries);

        // 서비스별 통계 (최근 24시간)
        Map<String, Object> services = getServiceStats(24);
        summary.put("services", services);

        summary.put("lastUpdated", System.currentTimeMillis());

        return summary;
    }
}
