package com.taskreminder.app.controller;
import com.taskreminder.app.service.TaskService;
import com.taskreminder.app.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/tasks")
public class TaskRestController {

    @Autowired
    private final TaskService taskservice;

    @Autowired
    public TaskRestController(TaskService taskservice){
        this.taskservice = taskservice;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable Integer id){
        Optional<Task> t = taskservice.findById(id);
        if(t.isPresent()){
            return new ResponseEntity<>(t.get(), HttpStatus.OK);
        }
        return t.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task task){
        task.setCreatedAt(LocalDateTime.now());
        Task saved = taskservice.addTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping
    public ResponseEntity<Task> update(@PathVariable Integer id,@RequestBody Task task){
        Optional<Task> existing = taskservice.findById(id);
        if(existing.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Task old = existing.get();
        old.setTitle(task.getTitle());
        old.setDescription(task.getDescription());
        old.setStatus(task.getStatus());
        old.setDueDate(task.getDueDate());

        Task updated = taskservice.updateTask(old);

        return ResponseEntity.ok(updated);
    }
}
