package com.taskreminder.app.service;
import com.taskreminder.app.dto.DashboardStatsDTO;
import com.taskreminder.app.entity.Task;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
   }

    public Task addTask(Task task){
        task.setUser(userService.getCurrentUser());
       return taskRepository.save(task);
    }

    public Task updateTask(Task task){
        User currentUser = userService.getCurrentUser();

        Task existingTask = taskRepository
                .findById(task.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!existingTask.getUser().equals(currentUser)) {
            throw new RuntimeException("Unauthorized");
        }

        if (task.getDueDate() != null) {
            LocalDateTime today = LocalDateTime.now();
            if (task.getDueDate().isBefore(today)) {
                throw new IllegalArgumentException("The due date cannot be earlier than today (" + today + ").");
            }
        }
        task.setUser(currentUser);
       return taskRepository.save(task);
    }

    public void deleteTask(Integer id){
        User currentUser = userService.getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().equals(currentUser)) {
            throw new RuntimeException("Unauthorized");
        }

        taskRepository.deleteById(id);
    }

    public Optional<Task> findById(Integer id){
        User currentUser = userService.getCurrentUser();
        return taskRepository.findById(id)
                .filter(task -> task.getUser().equals(currentUser));
    }


//Filters with pagination + sorting
public Page<Task> getPagedTasks(
        Pageable pageable,
        TaskStatus status,
        TaskPriority priority,
        String title
) { User user = userService.getCurrentUser();
    return taskRepository.findTasks(user,status, priority, title, pageable);
}

    public Page<Task> findAll(Pageable pageable) {
        User user = userService.getCurrentUser();
        return taskRepository.findByUser(user, pageable);
    }

    public List<Task> getTasksDueToday() {
        User user = userService.getCurrentUser();
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        return taskRepository.findByUserAndStatusNotAndDueDateBetween(user, TaskStatus.DONE,startOfDay,endOfDay);
    }

    public List<Task> getUpcomingTasks(int days) {
        User user = userService.getCurrentUser();
        LocalDateTime today = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime endDate = LocalDate.now().plusDays(days).atTime(LocalTime.MAX);
        return taskRepository.findByUserAndStatusAndDueDateBetween(
                user,
                TaskStatus.PENDING,
                today,
                endDate
        );
    }
    public List<Task> getOverdueTasks() {
        User user = userService.getCurrentUser();
        LocalDateTime today = LocalDateTime.now();
        return taskRepository.findByUserAndStatusNotAndDueDateBefore(
                user,
                TaskStatus.DONE,
                today
        );

    }

    public long countAllTasks() {
        User user = userService.getCurrentUser();
        return taskRepository.countByUser(user);
    }

    public long countFilteredTasks(
            TaskStatus status,
            TaskPriority priority,
            String title
    ) {User user = userService.getCurrentUser();
        return taskRepository.countTasks(user, status, priority, title);
    }

    public long countCompletedTasks(
            TaskStatus status,
            TaskPriority priority,
            String title
    ) {

        if (status != null && status != TaskStatus.DONE) {
            return 0;
        }
        User user = userService.getCurrentUser();
        return taskRepository.countCompletedTasks(user, priority, title);
    }
    public List<Task> findAllForCurrentUser() {
        User user = userService.getCurrentUser();
        return taskRepository.findByUser(user);
    }

    public DashboardStatsDTO getDashboardStats(User user) {
        long total = taskRepository.countByUser(user);
        long completed = taskRepository.countCompletedByUser(user);
        long pending = taskRepository.countPendingByUser(user);
        long overdue = taskRepository.countOverdueByUser(user);

        double rate = (total > 0) ? ((double) completed / total) * 100 : 0;

        return DashboardStatsDTO.builder()
                .totalTasks(total)
                .completedTasks(completed)
                .pendingTasks(pending)
                .overdueTasks(overdue)
                .completionRate(Math.round(rate * 10.0) / 10.0)
                .build();
    }

    public Map<String, Long> getDailyStats(User user, int days) {
        Map<String, Long> data = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = taskRepository.countCompletedByDate(user, date);

            // If range is large (like 30 days), "Mon" repeats too much.
            // Use "Jan 19" for longer ranges, "Mon" for shorter ranges.
            String label = (days > 7)
                    ? date.format(DateTimeFormatter.ofPattern("MMM dd"))
                    : date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            data.put(label, count);
        }
        return data;
    }
    @Transactional
    public void updateStatus(Integer taskId, TaskStatus status) {

        User user = userService.getCurrentUser();

        Task task = taskRepository
                .findByIdAndUser(taskId, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getStatus() == TaskStatus.DONE && status == TaskStatus.DONE) {
            return;
        }

        task.setStatus(status);

        if (status == TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }

        taskRepository.save(task);
    }



    public Task getTaskForCurrentUser(Integer taskId) {

        User currentUser = userService.getCurrentUser();

        return taskRepository.findById(taskId)
                .filter(task -> task.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() ->
                        new IllegalArgumentException("Task not found or access denied")
                );
    }
    @Transactional
    public void updateDueDateForCurrentUser(Integer taskId, LocalDateTime dueDate) {
        Task task = getTaskForCurrentUser(taskId);
        if (task.getStatus() == TaskStatus.DONE) {
            throw new IllegalStateException("Completed tasks cannot be moved");
        }
        task.setDueDate(dueDate);

        taskRepository.save(task);
    }


}
