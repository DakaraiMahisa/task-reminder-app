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
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("api/v1/tasks")
public class TaskRestController {
    @Autowired
    private final TaskService taskService;

    @Autowired
    public TaskRestController(TaskService taskService){
        this.taskService = taskService;
    }
    @GetMapping
    public ResponseEntity<Page<Task>> getAll(Pageable pageable){
        Page<Task> page = taskService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable Integer id){
        Optional<Task> t = taskService.findById(id);
        if(t.isPresent()){
            return new ResponseEntity<>(t.get(), HttpStatus.OK);
        }
        return t.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task task){
        task.setCreatedAt(LocalDateTime.now());
        Task saved = taskService.addTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Integer id,@RequestBody Task task){
        Optional<Task> existing = taskService.findById(id);
        if(existing.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Task old = existing.get();
        old.setTitle(task.getTitle());
        old.setDescription(task.getDescription());
        old.setStatus(task.getStatus());
        old.setDueDate(task.getDueDate());

        Task updated = taskService.updateTask(old);

        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        if(taskService.findById(id).isEmpty()) return ResponseEntity.notFound().build();
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/due-today")
    public ResponseEntity<List<Task>>getTaskDueToday(){
        return ResponseEntity.ok(taskService.getTaskDueToday());
    }
    @GetMapping("/upcoming")
    public ResponseEntity<List<Task>>getUpcomingTasks(@RequestParam(defaultValue = "3")int days){

        if (days <= 0) {
            throw new IllegalArgumentException("Days must be greater than zero");
        }

        return ResponseEntity.ok(taskService.getUpcomingTasks(days));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Task>>getOverdueTasks(){
        return ResponseEntity.ok(taskService.getOverdueTasks());
    }

}
