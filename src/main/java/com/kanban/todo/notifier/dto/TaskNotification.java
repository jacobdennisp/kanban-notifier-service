package com.kanban.todo.notifier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@SuppressWarnings("unused")

public class TaskNotification {
	@NotNull(message="Task Id is required")
	@JsonProperty("task_id")
	private Long taskId;
	
	@NotNull(message="Title is required")
	private String title;
	
	@NotNull(message="Status is required")
	private String status;
	
	@NotNull(message="Priority is required")
	private String priority;
	
	@NotNull(message="Action is required")
	private String action;
	
	@NotNull(message="Timestamp is required")
	private String timestamp;
	
	public LocalDateTime getTimestampAsDateTime() {
		return LocalDateTime.parse(timestamp.replace("Z", ""));
	}
	
	
	public String getFormattedMessage() {
		return String.format(
					"[%s] Task #%d: '%s' - Action: %s, Status: %s, Priority: %s",
					timestamp,
					taskId,
					title,
					action.toUpperCase(),
					status.toUpperCase(),
					priority.toUpperCase()
				);
	}
	public TaskNotification() {}
	
	public TaskNotification(@NotNull(message = "Task Id is required") Long taskId,
			@NotNull(message = "Title is required") String title,
			@NotNull(message = "Status is required") String status,
			@NotNull(message = "Priority is required") String priority,
			@NotNull(message = "Action is required") String action,
			@NotNull(message = "Timestamp is required") String timestamp) {
		this.taskId = taskId;
		this.title = title;
		this.status = status;
		this.priority = priority;
		this.action = action;
		this.timestamp = timestamp;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		// TODO Auto-generated method stub
		return action;
	}
}
