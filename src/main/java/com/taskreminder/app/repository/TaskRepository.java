package com.taskreminder.app.repository;
import com.taskreminder.app.entity.Task;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {


    @Query("""
    SELECT t FROM Task t
    WHERE t.user = :user
      AND (:status IS NULL OR t.status = :status)
      AND (:priority IS NULL OR t.priority = :priority)
      AND (:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')))
""")
    Page<Task> findTasks(
            @Param("user") User user,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("title") String title,
            Pageable pageable
    );
    Page<Task> findByUserAndStatus(User user,TaskStatus status, Pageable pageable);

    Page<Task> findByUserAndPriority(User user,TaskPriority priority, Pageable pageable);

    List<Task> findByUserAndStatusNotAndDueDate(User user,TaskStatus status,LocalDateTime dueDate);

    Page<Task> findByUserAndTitleContainingIgnoreCase(User user,String keyword,Pageable pageable);

    List<Task> findByUserAndStatusAndDueDateBetween(User user,TaskStatus status, LocalDateTime start, LocalDateTime end);
    List<Task> findByUserAndStatusNotAndDueDateBefore(User user,TaskStatus status,LocalDateTime date);
    Page<Task> findByUser(User user, Pageable pageable);
    List<Task> findByUser(User user);
    long countByUserAndStatus(User user,TaskStatus status);

    long countByUserAndPriority(User user,TaskPriority priority);

    long countByUserAndTitleContainingIgnoreCase(User user,String keyword);

    long countByUserAndStatusAndTitleContainingIgnoreCase(
            User user,TaskStatus status, String keyword
    );

    @Query("""
    SELECT COUNT(t) FROM Task t
    WHERE t.user = :user
      AND (:status IS NULL OR t.status = :status)
      AND (:priority IS NULL OR t.priority = :priority)
      AND (:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')))
  """)
    long countTasks(
            @Param("user") User user,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("title") String title
    );

    @Query("""
    SELECT COUNT(t) FROM Task t
    WHERE t.user = :user
      AND t.status = 'DONE'
      AND (:priority IS NULL OR t.priority = :priority)
      AND (:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')))
   """)
    long countCompletedTasks(
            @Param("user") User user,
            @Param("priority") TaskPriority priority,
            @Param("title") String title
    );

    List<Task> findByStatusAndReminderSentFalseAndDueDateBefore(TaskStatus status, LocalDateTime now);
    List<Task> findByStatusAndReminderSentFalseAndDueDateBetween(TaskStatus status, LocalDateTime now, LocalDateTime halfHourFromNow);

    long countByUser(User user);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user AND t.status = 'DONE'")
    long countCompletedByUser(@Param("user") User user);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user AND t.status = 'PENDING'")
    long countPendingByUser(@Param("user") User user);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user AND t.dueDate < CURRENT_DATE AND t.status != 'DONE'")
    long countOverdueByUser(@Param("user") User user);
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user " +
            "AND t.status = 'COMPLETED' " +
            "AND CAST(t.dueDate AS date) = :date")
    long countCompletedByDate(@Param("user") User user, @Param("date") LocalDate date);

    List<Task> findFirst5ByUserOrderByDueDateDesc(User user);
}
