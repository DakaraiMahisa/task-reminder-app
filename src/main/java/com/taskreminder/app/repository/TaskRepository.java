package com.taskreminder.app.repository;
import com.taskreminder.app.entity.Task;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {


    @Query("""
        SELECT t FROM Task t
        WHERE (:status IS NULL OR t.status = :status)
          AND (:priority IS NULL OR t.priority = :priority)
          AND (:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')))
    """)
    Page<Task> findTasks(
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("title") String title,
            Pageable pageable
    );
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByPriority(TaskPriority priority, Pageable pageable);

    List<Task> findByDueDate(String dueDate);

    Page<Task> findByTitleContainingIgnoreCase(String keyword,Pageable pageable);

    List<Task> findByDueDateBetween( String start, String end);
    List<Task> findByDueDateBefore(String date);
}
