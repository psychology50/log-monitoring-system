package com.logmonitoring.log_processor.dto;

import java.time.LocalDateTime;

public record LogEntry(
        String id,
        String message,
        String level,
        String service,
        LocalDateTime timestamp
) {
    public LogEntry {
        if (id == null || id.isBlank()) {
            id = java.util.UUID.randomUUID().toString();
        }

        if (level == null || level.isBlank()) {
            level = "INFO"; // Default log level
        }

        if (service == null) {
            service = "unknown"; // Default service name
        }

        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public static LogEntry info(String message, String service) {
        return new LogEntry(
                java.util.UUID.randomUUID().toString(),
                message,
                "INFO",
                service,
                LocalDateTime.now()
        );
    }

    public static LogEntry error(String message, String service) {
        return new LogEntry(
                java.util.UUID.randomUUID().toString(),
                message,
                "ERROR",
                service,
                LocalDateTime.now()
        );
    }

    public static LogEntry warn(String message, String service) {
        return new LogEntry(
                java.util.UUID.randomUUID().toString(),
                message,
                "WARN",
                service,
                LocalDateTime.now()
        );
    }

    public static LogEntry debug(String message, String service) {
        return new LogEntry(
                java.util.UUID.randomUUID().toString(),
                message,
                "DEBUG",
                service,
                LocalDateTime.now()
        );
    }

    public static LogEntry createInstance(String message, String level, String service) {
        return new LogEntry(
                java.util.UUID.randomUUID().toString(),
                message,
                level,
                service,
                LocalDateTime.now()
        );
    }
}
