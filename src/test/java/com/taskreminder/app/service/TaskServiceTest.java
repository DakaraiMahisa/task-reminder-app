package com.taskreminder.app.service;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService service;

    private Pageable pageable;

    @BeforeEach
    void setup(){
        pageable = PageRequest.of(0,10);
    }


    @Test
    void testGetAllTasks() {
        List<Task> mockTasks = List.of(new Task(), new Task());

        when(taskRepository.findAll()).thenReturn(mockTasks);

        // Act
        List<Task> result = service.getAllTasks();

        // Assert
        assertEquals(2, result.size());
        verify(taskRepository,times(1)).findAll();
    }

    @Test
    void TestAddTask() {
        Task task = new Task();

        when(taskRepository.save(task)).thenReturn(task);

        Task result = service.addTask(task);

        assertNotNull(result);
        verify(taskRepository,times(1)).save(task);
    }
    @Test
    void TestUpdateTask() {
        Task task = new Task();

        when(taskRepository.save(task)).thenReturn(task);

        Task result = service.addTask(task);

        assertEquals(task,result);
        verify(taskRepository,times(1)).save(task);
    }

    @Test
    void TestDeleteTask() {
        Integer taskId = 1;

        service.deleteTask(taskId);

        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void TestFindById() {
        Task task = new Task();
        Optional<Task> optionalTask = Optional.of(task);

        when(taskRepository.findById(1)).thenReturn(optionalTask);

        Optional<Task> result = service.findById(1);

        assertTrue(result.isPresent());
        verify(taskRepository).findById(1);
    }

    @Test
    void TestGetPagedTasksFilterByStatus(){
        Page<Task> page = new PageImpl<>(List.of(new Task()));
        when(taskRepository.findByStatus(TaskStatus.PENDING,pageable)).thenReturn(page);

        Page<Task> result = service.getPagedTasks(pageable,TaskStatus.PENDING,null,null);

        assertEquals(1,result.getTotalElements());
        verify(taskRepository).findByStatus(TaskStatus.PENDING,pageable);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void TestGetPagedTasksFilterByPriority(){
        Page<Task> page = new PageImpl<>(List.of(new Task()));
        when(taskRepository.findByPriority(TaskPriority.HIGH,pageable)).thenReturn(page);

        Page<Task> result = service.getPagedTasks(pageable,null,TaskPriority.HIGH,null);

        assertEquals(1,result.getTotalElements());
        verify(taskRepository).findByPriority(TaskPriority.HIGH,pageable);
    }

    @Test
    void TestGetPagedTasksFilterByKeyword(){
        Page<Task> page = new PageImpl<>(List.of(new Task()));
        when(taskRepository.findByTitleContainingIgnoreCase("test",pageable)).thenReturn(page);

        Page<Task> result = service.getPagedTasks(pageable,null,null,"test");

        assertEquals(1,result.getTotalElements());
        verify(taskRepository).findByTitleContainingIgnoreCase("test",pageable);
    }

    @Test
    void TestGetPagedTasksNoFilterProvided(){
        Page<Task> page = new PageImpl<>(List.of(new Task()));
        when(taskRepository.findAll(pageable)).thenReturn(page);

        Page<Task> result = service.getPagedTasks(pageable,null,null,null);

        assertEquals(1,result.getTotalElements());
        verify(taskRepository).findAll(pageable);
    }

    @Test
    void TestFindAll(){
        Page<Task> page = new PageImpl<>(List.of(new Task()));
        when(taskRepository.findAll(pageable)).thenReturn(page);

        Page<Task> result = service.findAll(pageable);

        assertEquals(1,result.getTotalElements());
        verify(taskRepository).findAll(pageable);

    }

    @Test
    void TestGetTaskDueToday(){
        Task done = new Task();
        done.setStatus(TaskStatus.DONE);
        Task pending = new Task();
        pending.setStatus(TaskStatus.PENDING);

        String today = LocalDate.now().toString();
        when(taskRepository.findByDueDate(today)).thenReturn(List.of(done,pending));


        List<Task> result = service.getTaskDueToday();

        assertEquals(1,result.size());
        assertEquals(TaskStatus.PENDING,result.get(0).getStatus());

    }

    @Test
    void TestGetUpcomingTasks(){
        Task pending = new Task();
        pending.setStatus(TaskStatus.PENDING);


        Task done = new Task();
        done.setStatus(TaskStatus.DONE);

        when(taskRepository.findByDueDateBetween(anyString(),anyString())).thenReturn(List.of(pending,done));


        List<Task> result = service.getUpcomingTasks(5);

        assertEquals(1,result.size());
        assertEquals(TaskStatus.PENDING,result.get(0).getStatus());

    }
    @Test
    void TestGetOverdueTasks(){
        Task pending = new Task();
        pending.setStatus(TaskStatus.PENDING);


        Task done = new Task();
        done.setStatus(TaskStatus.DONE);

        when(taskRepository.findByDueDateBefore(anyString())).thenReturn(List.of(pending,done));


        List<Task> result = service.getOverdueTasks();

        assertEquals(1,result.size());
        assertEquals(TaskStatus.PENDING,result.get(0).getStatus());
    }

    @Test
    void TestCountAllTasks(){
        when(taskRepository.count()).thenReturn(10L);

        long count = service.countAllTasks();

        assertEquals(10L,count);
        verify(taskRepository).count();
    }

    @Test
    void TestCountFilteredTasks(){
        when(taskRepository.countByTitleContainingIgnoreCase("task")).thenReturn(2L);

        long count = service.countFilteredTasks(null,null,"task");

        assertEquals(2L,count);
    }
    @Test
    void TestCountCompletedTasksWhenStatusNotDone(){
        long count = service.countCompletedTasks(TaskStatus.PENDING,null,null);

        assertEquals(0L,count);
        verifyNoInteractions(taskRepository);
    }

    @Test
    void TestCountCompletedTasksWhenStatusDone(){
        when(taskRepository.countByStatus(TaskStatus.DONE)).thenReturn(5L);

        long count = service.countCompletedTasks(TaskStatus.DONE,null,null);

        assertEquals(5L,count);
    }
    @Test
    void TestCountCompletedTasksWhenDoneByKeyword(){
        when(taskRepository.countByStatusAndTitleContainingIgnoreCase(TaskStatus.DONE,"report")).thenReturn(2L);

        long count = service.countCompletedTasks(null,null,"report");

        assertEquals(2L,count);
    }

}
