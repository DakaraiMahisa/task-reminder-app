package com.taskreminder.app.scheduler;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.repository.TaskRepository;
import com.taskreminder.app.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TaskReminderScheduler {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmailService emailService;

    /**
     * This method runs every 15 minutes.
     * It looks for PENDING tasks due in the next 30 minutes that haven't been notified.
     */
    @Scheduled(cron = "0 0/15 * * * *")
    public void sendUpcomingTaskReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime halfHourFromNow = now.plusMinutes(30);

        // Fetch tasks due soon
        List<Task> upcomingTasks = taskRepository
                .findByStatusAndReminderSentFalseAndDueDateBetween(
                        TaskStatus.PENDING,
                        now,
                        halfHourFromNow
                );
        System.out.println("Tasks found to notify: " + upcomingTasks.size());
        for (Task task : upcomingTasks) {
            sendEmail(task, "Upcoming Task Reminder");
        }
    }

    /**
     * This method runs once an hour to catch tasks that became overdue.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void sendOverdueTaskReminders() {
        LocalDateTime now = LocalDateTime.now();

        // Fetch overdue tasks that haven't had a reminder sent
        List<Task> overdueTasks = taskRepository
                .findByStatusAndReminderSentFalseAndDueDateBefore(
                        TaskStatus.PENDING,
                        now
                );

        for (Task task : overdueTasks) {
            sendEmail(task, "URGENT: Task Overdue");
        }
    }

    private void sendEmail(Task task, String subject) {
        try {
            String htmlBody = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden;">
                <div style="background-color: #007bff; padding: 20px; text-align: center;">
                    <h1 style="color: white; margin: 0; font-size: 24px;">Task Reminder</h1>
                </div>
                <div style="padding: 30px; background-color: #ffffff;">
                    <p style="font-size: 16px; color: #4a5568;">Hello <strong>%s</strong>,</p>
                    <p style="font-size: 16px; color: #4a5568;">Your task is due soon. Here are the details:</p>
                    
                    <div style="background-color: #f7fafc; border-left: 4px solid #007bff; padding: 15px; margin: 20px 0;">
                        <h2 style="margin: 0; color: #2d3748; font-size: 18px;">%s</h2>
                        <p style="margin: 5px 0 0 0; color: #718096; font-size: 14px;">Due: %s</p>
                    </div>

                    <a href="http://localhost:8080/tasks" 
                       style="display: inline-block; background-color: #007bff; color: white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold; margin-top: 10px;">
                       View Task Details
                    </a>
                </div>
                <div style="background-color: #f7fafc; padding: 15px; text-align: center; font-size: 12px; color: #a0aec0;">
                    Automated reminder from your Task Manager App.
                </div>
            </div>
            """.formatted(
                    task.getUser().getName(),
                    task.getTitle(),
                    task.getDueDate().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"))
            );

            emailService.sendEmail(task.getUser().getEmail(), subject, htmlBody);


            task.setReminderSent(true);
            taskRepository.save(task);
        } catch (Exception e) {

            System.err.println("Failed to send email for task ID: " + task.getId());
        }
    }
}