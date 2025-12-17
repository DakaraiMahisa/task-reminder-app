package com.taskreminder.app.entity;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;


@Entity
@Table(name="tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="task_id")
    private Integer id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;

    @NotBlank(message="Description is required")
    private String description;

    @NotBlank(message = "Due date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Due date must be in YYYY-MM-DD format")
    private String dueDate;

    @NotNull(message="Status is required")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @NotNull(message="Priority is required")
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    private LocalDateTime createdAt;


    private LocalDateTime completedAt;

    public Task(){}

   public Task(Integer id,String title,String description,String dueDate,TaskStatus status,TaskPriority priority,LocalDateTime createdAt,LocalDateTime completedAt){
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status =  status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
