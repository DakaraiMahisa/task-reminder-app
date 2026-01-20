package com.taskreminder.app.scheduler;

import com.taskreminder.app.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
public class TrashCleanupScheduler {
    @Autowired
    private TaskRepository taskRepository;
    @Scheduled(cron = "0 0 3 * * ?")
    public void purgeOldDeletedTasks() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        taskRepository.deleteByDeletedTrueAndDeletedAtBefore(cutoff);
    }
}
