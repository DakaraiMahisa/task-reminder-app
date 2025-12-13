package com.taskreminder.app.controller;
import com.taskreminder.app.service.TaskService;
import com.taskreminder.app.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
@RestController
@RequestMapping("api/v1/tasks")
public class TaskRestController {

    @Autowired
    private final TaskService taskservice;

    @Autowired
    public TaskRestController(TaskService taskservice){
        this.taskservice = taskservice;
    }

    @GetMapping
    public ResponseEntity<Page<Task>> getAll(Pageable pageable){
        Page<Task> page = taskservice.findAll(pageable);
        return ResponseEntity.ok(page);
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

    @PutMapping("/{id}")
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


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        if(taskservice.findById(id).isEmpty()) return ResponseEntity.notFound().build();
        taskservice.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("status/{status}")
    public ResponseEntity<List<Task>> getByPriority(@PathVariable String status){
        return ResponseEntity.ok(taskservice.filterByStatus(status));
    }

    @GetMapping("priority/{priority}")
    public ResponseEntity<List<Task>> getByStatus(@PathVariable String priority){
        return ResponseEntity.ok(taskservice.filterByPriority(priority));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchByTitle(@RequestParam("keyword") String keyword){
        return ResponseEntity.ok(taskservice.searchByTitle(keyword));
    }

    @GetMapping("/due")
    public ResponseEntity<List<Task>> getByDueDate(@RequestParam("due") String date){
        return ResponseEntity.ok(taskservice.filterByDueDate(date));
    }



}
