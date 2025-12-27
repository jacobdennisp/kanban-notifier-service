package com.kanban.todo.notifier.service;

import com.kanban.todo.notifier.dto.TaskNotification;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import javax.management.Notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@SuppressWarnings("unused")

@Service
public class NotificationService {

	private static final Logger log=LoggerFactory.getLogger(NotificationService.class);
	@Async
	public void processNotification(TaskNotification notification) {

		log.info("Precessing Notifacations asyncronously");
		
		try {
			Thread.sleep(100);
			switch(notification.getAction().toLowerCase()) {
				case "created" : handleTaskCreated(notification);
								break;
				case "updated" : handleTaskUpdated(notification);
								break;
				case "status_changed" : handleStatusChanged(notification);
								break;
				case "deleted" : handleTaskDeleted(notification);
								break;
				default : log.warn("Uknow action type:{}",notification.getAction());
				
			}
			 log.info("Notification processed successfully for Task #{}",notification.getTaskId());
		}catch(InterruptedException e){
			Thread.currentThread().interrupt();
			log.error("Notification processing Interupted",e);
			
		}catch(Exception e) {
			log.error("Error processing notification",e);
			throw new RuntimeException("Failed to process notification",e);
		}
			
	}
	
	private void handleTaskCreated(TaskNotification notification) {
		log.info("New Task Created");
	}

	private void handleTaskUpdated(TaskNotification notification) {
		log.info("Task Updated");
	}
	
	private void handleStatusChanged(TaskNotification notification) {
		log.info("Task Status changed");
	}
	
	private void handleTaskDeleted(TaskNotification notification) {
		log.info("Task Deleted");
	}
	
	public CompletableFuture<NotificationStats> getStats(){
		return CompletableFuture.supplyAsync(() -> {
			NotificationStats stats = new NotificationStats();
			stats.setTotalProcessed(100L);
			stats.setSuccessCount(95L);
			stats.setFailureCount(5L);
			stats.setLastProcessed(LocalDateTime.now());
			return stats;
		});
	}
	
	public static class NotificationStats{
		private Long totalProcessed;
		private Long successCount;
		private Long failureCount;
		private LocalDateTime lastProcessed;
		public NotificationStats() {
		}
		public Long getTotalProcessed() {
			return totalProcessed;
		}
		public void setTotalProcessed(Long totalProcessed) {
			this.totalProcessed=totalProcessed;
		}
		public Long getSuccessCount() {
			return successCount;
		}
		public void setSuccessCount(Long successCount) {
			this.successCount = successCount;
		}
		public Long getFailureCount() {
			return failureCount;
		}
		public void setFailureCount(Long failureCount) {
			this.failureCount = failureCount;
		}
		public LocalDateTime getLastProcessed() {
			return lastProcessed;
		}
		public void setLastProcessed(LocalDateTime lastProcessed) {
			this.lastProcessed = lastProcessed;
		}
	}
}
