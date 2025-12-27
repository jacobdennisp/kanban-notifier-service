package com.kanban.todo.notifier.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanban.todo.notifier.dto.TaskNotification;
import com.kanban.todo.notifier.service.NotificationService;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class TaskNotificationListener {
	private static final Logger log = LoggerFactory.getLogger(TaskNotificationListener.class);
	private final NotificationService notificationService;
	private final ObjectMapper objectMapper;
    public TaskNotificationListener(NotificationService notificationService,ObjectMapper objectMapper) {
    	this.notificationService = notificationService;
    	this.objectMapper = objectMapper;
    }
		@RabbitListener(queues="${rabbitmq.queue.name}")
		public void handleTasknotification(Message message) {
			try {
				String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
				log.info("Received Messagte from queue:{}",messageBody);
				TaskNotification notification = objectMapper.readValue(messageBody,TaskNotification.class);
				log.info("Parsed notification Task #{} - Action: {} - Status:{}",
						notification.getTaskId(),
						notification.getAction(),
						notification.getStatus());
				notificationService.processNotification(notification);
				log.info("Successfully proceed notification for Task #{}",notification.getTaskId());
				
			}catch(Exception e) {
				log.error("Error process notificatio: {}",e.getMessage(),e);
				throw new RuntimeException("Failed to process notification",e);
			}
		}
		
		public void handleError(Throwable throwable) {
			log.error("Error is rabbitmq: {}",throwable.getMessage(), throwable);
		}
		

}
