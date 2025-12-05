package com.taskreminder.app.entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class Task {

    private Integer id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;

    @NotBlank(message="Description is required")
    private String description;

    @NotBlank(message = "Due date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Due date must be in YYYY-MM-DD format")
    private String dueDate;

    @NotBlank(message="Status is required")
    private String status;

    @NotBlank(message="Priority is required")
    private String priority;
    private LocalDateTime createdAt;

    public Task(){}

   public Task(Integer id,String title,String description,String dueDate,String status,String priority,LocalDateTime createdAt){
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status =  status;
        this.priority = priority;
        this.createdAt = createdAt;
   }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
