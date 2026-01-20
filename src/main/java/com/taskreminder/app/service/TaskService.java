package com.taskreminder.app.service;
import com.taskreminder.app.dto.DashboardStatsDTO;
import com.taskreminder.app.entity.Task;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.repository.TaskRepository;
import com.taskreminder.app.security.UndoConfig;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    public void markTaskDone(Integer taskId) {
        Task task = getTaskForCurrentUser(taskId);

        task.setStatus(TaskStatus.DONE);
        task.setCompletedAt(LocalDateTime.now());

        taskRepository.save(task);
    }
    public boolean undoTaskCompletion(Integer taskId) {
        Task task = getTaskForCurrentUser(taskId);

        if (task.getCompletedAt() == null) return false;

        Duration elapsed = Duration.between(
                task.getCompletedAt(),
                LocalDateTime.now()
        );

        if (elapsed.compareTo(UndoConfig.COMPLETION_UNDO_WINDOW) > 0) {
            return false;
        }

        task.setStatus(TaskStatus.PENDING);
        task.setCompletedAt(null);

        taskRepository.save(task);
        return true;
    }
    public void softDeleteTask(Integer taskId) {
        Task task = getTaskForCurrentUser(taskId);

        task.setDeleted(true);
        task.setDeletedAt(LocalDateTime.now());

        taskRepository.save(task);
    }
    public void softDeleteTasks(List<Integer> taskIds) {
        List<Task> tasks = taskRepository
                .findAllById(taskIds)
                .stream()
                .filter(t -> t.getUser().equals(userService.getCurrentUser()))
                .toList();

        tasks.forEach(t -> {
            t.setDeleted(true);
            t.setDeletedAt(LocalDateTime.now());
        });

        taskRepository.saveAll(tasks);
    }

    public List<Task> getDeletedTasksForCurrentUser() {
        return taskRepository.findByUserAndDeletedTrue(
                userService.getCurrentUser()
        );
    }

    public void restoreTask(Integer taskId) {
        Task task = taskRepository
                .findByIdAndUser(taskId, userService.getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.isDeleted()) {
            return;
        }

        task.setDeleted(false);
        task.setDeletedAt(null);

        taskRepository.save(task);
    }

    public void restoreTasks(List<Integer> taskIds) {
        User user = userService.getCurrentUser();

        List<Task> tasks = taskRepository.findAllById(taskIds);

        tasks.stream()
                .filter(task ->
                        task.isDeleted()
                                && task.getUser().equals(user))
                .forEach(task -> {
                    task.setDeleted(false);
                    task.setDeletedAt(null);
                });

        taskRepository.saveAll(tasks);
    }

    public void permanentlyDeleteTask(Integer taskId) {
        Task task = taskRepository
                .findByIdAndUser(taskId, userService.getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.isDeleted()) {
            throw new IllegalStateException("Task must be deleted first");
        }

        taskRepository.delete(task);
    }

    public void permanentlyDeleteTasks(List<Integer> taskIds) {
        User user = userService.getCurrentUser();

        List<Task> tasks = taskRepository.findAllById(taskIds);

        tasks.stream()
                .filter(task ->
                        task.isDeleted()
                                && task.getUser().equals(user))
                .forEach(taskRepository::delete);
    }

}
