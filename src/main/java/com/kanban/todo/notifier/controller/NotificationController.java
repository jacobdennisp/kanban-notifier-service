package com.kanban.todo.notifier.controller;

import com.kanban.todo.notifier.dto.TaskNotification;
import com.kanban.todo.notifier.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Notification API Controller
 * 
 * REST endpoints for notification service.
 * Used by Laravel API to send notifications via HTTP.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;
    public NotificationController(NotificationService notificationService) {
    	this.notificationService = notificationService;
    }
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Kanban Notification Service");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Service is running and ready to receive notifications");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Receive notification via HTTP POST
     * 
     * Alternative to RabbitMQ for direct HTTP notifications
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> receiveNotification(
            @Valid @RequestBody TaskNotification notification) {
        
        log.info("Received HTTP notification: Task #{} - Action: {}", 
                notification.getTaskId(), 
                notification.getAction());

        try {
            // Process notification asynchronously
            notificationService.processNotification(notification);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification received and queued for processing");
            response.put("task_id", notification.getTaskId());
            response.put("action", notification.getAction());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

        } catch (Exception e) {
            log.error("Error processing HTTP notification", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to process notification");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get notification statistics
     */
    @GetMapping("/stats")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getStats() {
        return notificationService.getStats()
                .thenApply(stats -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", stats);
                    return ResponseEntity.ok(response);
                })
                .exceptionally(e -> {
                    log.error("Error fetching stats", e);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Failed to fetch statistics");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    /**
     * Test endpoint to verify notification processing
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testNotification() {
        TaskNotification testNotification = new TaskNotification(
        		999L,
                "Test Notification",
                "todo",
                "high",
                "created",
                LocalDateTime.now().toString()
         );

        notificationService.processNotification(testNotification);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Test notification sent");
        response.put("notification", testNotification);

        return ResponseEntity.ok(response);
    }

    /**
     * Exception handler for validation errors
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("Controller exception", e);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", e.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}