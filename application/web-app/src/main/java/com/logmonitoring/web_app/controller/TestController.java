package com.logmonitoring.web_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public TestController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/test")
    public String test(@RequestParam(defaultValue = "Hello Kafka!") String message) {
        kafkaTemplate.send("app-logs", message);
        return "Message sent: " + message;
    }

    @GetMapping("/health")
    public String health() {
        return "Web App is running on port 8081";
    }
}