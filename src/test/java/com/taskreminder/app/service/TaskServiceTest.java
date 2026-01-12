package com.taskreminder.app.service;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.entity.User;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

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

        List<Task> result = service.getAllTasks();
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
    void TestUpdateTask_ShouldSave_WhenUserIsOwner() {
        User user = new User();
        Task existingTask = new Task();
        existingTask.setId(1);
        existingTask.setUser(user);

        Task updatedDetails = new Task();
        updatedDetails.setId(1);
        updatedDetails.setTitle("Updated Title");

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(1)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        Task result = service.updateTask(updatedDetails);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(user, result.getUser());
        verify(taskRepository).save(updatedDetails);
    }
    @Test
    void TestUpdateTask_ShouldThrowUnauthorized_WhenUserIsNotOwner() {
        User currentUser = new User();
        User otherUser = new User();

        Task existingTask = new Task();
        existingTask.setId(1);
        existingTask.setUser(otherUser);

        Task taskToUpdate = new Task();
        taskToUpdate.setId(1);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findById(1)).thenReturn(Optional.of(existingTask));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.updateTask(taskToUpdate));

        assertEquals("Unauthorized", ex.getMessage());
        verify(taskRepository, never()).save(any());
    }
    @Test
    void TestUpdateTask_ShouldThrowNotFound_WhenTaskDoesNotExit() {
        Task taskToUpdate = new Task();
        taskToUpdate.setId(99);

        when(userService.getCurrentUser()).thenReturn(new User());
        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.updateTask(taskToUpdate));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void TestDeleteTask_WhenUserIsOwner() {
        Integer taskId = 1;
        User user = new User();
        Task task = new Task();
        task.setUser(user);

        when(userService.getCurrentUser()).thenReturn(user);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertDoesNotThrow(() -> service.deleteTask(taskId));

        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void TestDeleteTask_WhenUserIsNotOwner(){
        Integer taskId = 1;
        User currentUser = new User();
        User otherUser = new User();

        Task task = new Task();
        task.setUser(otherUser);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteTask(taskId));
        assertEquals("Unauthorized", ex.getMessage());
        verify(taskRepository, never()).deleteById(any());
    }
    @Test
    void TestFindById_WhenUserIsOwner() {
        Integer taskId = 1;
        User user = new User();
        Task task = new Task();
        task.setUser(user);

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Optional<Task> result = service.findById(taskId);

        assertTrue(result.isPresent());
        assertEquals(task, result.get());
    }
    @Test
    void TestFindById_WhenUserIsNotOwner() {
        Integer taskId = 1;
        User currentUser = new User();
        User otherUser = new User();
        Task task = new Task();
        task.setUser(otherUser);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Optional<Task> result = service.findById(taskId);
        assertTrue(result.isEmpty());
    }
    @Test
    void TestGetPagedTasks(){
        User mockUser = new User();
        Pageable pageable = PageRequest.of(0, 10);
        TaskStatus status = TaskStatus.DONE;
        TaskPriority priority = TaskPriority.HIGH;
        String title = "Report";

        Page<Task> expectedPage = new PageImpl<>(Collections.emptyList());

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(taskRepository.findTasks(mockUser, status, priority, title, pageable))
                .thenReturn(expectedPage);

        Page<Task> result = service.getPagedTasks(pageable, status, priority, title);

        assertNotNull(result);
        assertEquals(expectedPage, result);

        verify(userService, times(1)).getCurrentUser();
        verify(taskRepository, times(1)).findTasks(mockUser, status, priority, title, pageable);
    }

    @Test
    void TestFindAll(){
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("testUser");

        Pageable pageable = PageRequest.of(0, 5);
        List<Task> taskList = List.of(new Task(), new Task());
        Page<Task> expectedPage = new PageImpl<>(taskList);

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(taskRepository.findByUser(mockUser, pageable)).thenReturn(expectedPage);
        Page<Task> result = service.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        verify(userService, times(1)).getCurrentUser();
        verify(taskRepository, times(1)).findByUser(mockUser, pageable);
        verify(taskRepository, never()).findAll(any(Pageable.class));

    }


    @Test
    void TestCountAllTasks(){
        User mockUser = new User();
        mockUser.setId(101);
        long expectedCount = 5L;

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(taskRepository.countByUser(mockUser)).thenReturn(expectedCount);

        long result = service.countAllTasks();

        assertEquals(expectedCount, result);

        verify(userService, times(1)).getCurrentUser();
        verify(taskRepository, times(1)).countByUser(mockUser);

        verify(taskRepository, never()).count();
    }

    @Test
    void TestCountFilteredTasks(){
        User mockUser = new User();
        TaskStatus status = TaskStatus.IN_PROGRESS;
        TaskPriority priority = TaskPriority.MEDIUM;
        String title = "Refactor";
        long expectedCount = 3L;

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(taskRepository.countTasks(mockUser, status, priority, title))
                .thenReturn(expectedCount);
        long result = service.countFilteredTasks(status, priority, title);
        assertEquals(expectedCount, result);

        verify(userService).getCurrentUser();
        verify(taskRepository).countTasks(mockUser, status, priority, title);
    }
    @Test
    void TestCountCompletedTasksWhenStatusNotDone(){
        TaskStatus status = TaskStatus.IN_PROGRESS;
        long result = service.countCompletedTasks(status, TaskPriority.HIGH, "Title");
        assertEquals(0, result);
        verifyNoInteractions(userService);
        verifyNoInteractions(taskRepository);
    }

    @Test
    void TestCountCompletedTasksWhenStatusDone(){
        User mockUser = new User();
        TaskStatus status = TaskStatus.DONE;
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(taskRepository.countCompletedTasks(mockUser, TaskPriority.LOW, "Fix"))
                .thenReturn(10L);

        long result = service.countCompletedTasks(status, TaskPriority.LOW, "Fix");

        assertEquals(10, result);
        verify(taskRepository).countCompletedTasks(mockUser, TaskPriority.LOW, "Fix");
    }
    @Test
    void TestCountCompletedTasksWhenDoneByKeyword(){
        User mockUser = new User();
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(taskRepository.countCompletedTasks(mockUser, null, null))
                .thenReturn(5L);

        long result = service.countCompletedTasks(null, null, null);
        assertEquals(5, result);
        verify(userService).getCurrentUser();
    }

    @Test
    void TestFindAllForCurrentUser() {
        User mockUser = new User();
        mockUser.setId(1);

        List<Task> mockTasks = Arrays.asList(new Task(), new Task());

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(taskRepository.findByUser(mockUser)).thenReturn(mockTasks);

        List<Task> result = service.findAllForCurrentUser();


        assertNotNull(result);
        assertEquals(2, result.size(), "Should return the exact list of tasks from repository");

        verify(userService, times(1)).getCurrentUser();
        verify(taskRepository, times(1)).findByUser(mockUser);
    }

}
