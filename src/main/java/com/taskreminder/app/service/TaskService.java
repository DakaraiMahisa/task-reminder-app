package com.taskreminder.app.service;
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
import java.util.List;
import java.util.Optional;

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

    public List<Task> getTaskDueToday() {
        User user = userService.getCurrentUser();
        LocalDate today = LocalDate.now();

        return taskRepository.findByUserAndDueDate(user, today.toString())
                .stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .toList();
    }

    public List<Task> getUpcomingTasks(int days) {
        User user = userService.getCurrentUser();
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        return taskRepository.findByUserAndDueDateBetween(
                        user,
                        today.plusDays(1).toString(),
                        endDate.toString()
                ).stream()
                .filter(task -> task.getStatus() == TaskStatus.PENDING)
                .toList();
    }

    public List<Task> getOverdueTasks() {
        User user = userService.getCurrentUser();
        LocalDate today = LocalDate.now();

        return taskRepository.findByUserAndDueDateBefore(user, today.toString())
                .stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .toList();
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

}
