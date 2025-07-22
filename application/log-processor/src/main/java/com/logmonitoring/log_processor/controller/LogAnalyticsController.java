package com.logmonitoring.log_processor.controller;

import com.logmonitoring.log_processor.service.LogAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class LogAnalyticsController {
    private final LogAnalyticsService analyticsService;

    @Autowired
    public LogAnalyticsController(LogAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardSummary() {
        return analyticsService.getDashboardSummary();
    }

    @GetMapping("/levels")
    public Map<String, Object> getLogLevelStats(@RequestParam(defaultValue = "24") int hours) {
        return analyticsService.getLogLevelStats(hours);
    }

    @GetMapping("/timeline")
    public Map<String, Object> getTimeSeriesStats(@RequestParam(defaultValue = "6") int hours) {
        return analyticsService.getTimeSeriesStats(hours);
    }

    @GetMapping("/services")
    public Map<String, Object> getServiceStats(@RequestParam(defaultValue = "24") int hours) {
        return analyticsService.getServiceStats(hours);
    }
}
