package com.taskreminder.app.entity;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Entity
@Table(name="tasks")
@Data
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

    @NotNull(message = "Due date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dueDate;

    @NotNull(message="Status is required")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @NotNull(message="Priority is required")
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column
    private LocalDateTime deletedAt;
    private LocalDateTime completedAt;

    @Column(name = "reminder_sent",nullable = false)
    private Boolean reminderSent = false;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    public Task(){}


    @Transient
    public boolean isOverdue() {
        return dueDate != null
                && status != TaskStatus.DONE
                && LocalDateTime.now().isAfter(dueDate);
    }

    @Transient
    public long getDaysRemaining() {
        if (deletedAt == null) return 0;

        return ChronoUnit.DAYS.between(
                LocalDateTime.now(),
                deletedAt.plusDays(30)
        );
    }
}
