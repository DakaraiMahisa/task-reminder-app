package com.taskreminder.app.service;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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


}
