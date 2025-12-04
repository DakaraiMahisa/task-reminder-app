package com.taskreminder.app.service;

import com.taskreminder.app.entity.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class TaskService {
    private final List<Task>tasks = new ArrayList<>();
    private int nextId = 10;
    public TaskService(){
        tasks.add(new Task(1,"Learn Spring Boot","Basics of project","2025-12-25","pending","high",LocalDateTime.now()));
        tasks.add(new Task(2,"Practice Java","Collection and OOP","2025-12-26","pending","medium",LocalDateTime.now()));
        tasks.add(new Task(3,"Practice Python","TensorFlow","2025-12-27","pending","high",LocalDateTime.now()));
        tasks.add(new Task(4,"Learn HTML","web page layout","2025-12-28","pending","medium",LocalDateTime.now()));
        tasks.add(new Task(5,"Lean C","Embedded C programming","2025-12-29","pending","high",LocalDateTime.now()));
        tasks.add(new Task(6,"Computer networks","TCP/UDP protocol","2025-12-26","pending","medium",LocalDateTime.now()));
        tasks.add(new Task(7,"SDN","Multi-tenant Data centers","2025-12-30","pending","high",LocalDateTime.now()));
    }
    public List<Task> getAllTasks(){
        return tasks;
    }

    public Task getTaskById(int id) {
        return tasks.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    public void addTask(Task task) {
        task.setId(nextId++);
        task.setCreatedAt(LocalDateTime.now());
        tasks.add(task);
    }

    public void updateTask(Task updatedTask){
        Task existing  = getTaskById(updatedTask.getId());
        if(existing!=null){
            existing.setTitle(updatedTask.getTitle());
            existing.setDescription(updatedTask.getDescription());
            existing.setDueDate(updatedTask.getDueDate());
            existing.setStatus(updatedTask.getStatus());
            existing.setPriority(updatedTask.getPriority());
        }
    }

    public void deleteTask(int id){
        tasks.removeIf(t->t.getId()==id);
    }

}
