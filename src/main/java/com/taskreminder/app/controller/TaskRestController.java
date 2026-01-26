package com.taskreminder.app.controller;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.service.TaskService;
import com.taskreminder.app.entity.Task;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

   @PostMapping("/add")
   public ResponseEntity<?> createTask(@Valid @RequestBody Task task,
                                       BindingResult result,
                                       HttpSession session) {
       if (result.hasErrors()) {
           List<String> errors = result.getFieldErrors()
                   .stream()
                   .map(err -> err.getField() + ": " + err.getDefaultMessage())
                   .collect(Collectors.toList());
           return ResponseEntity.badRequest().body(errors);
       }

       try {
           if (task.getDueDate() == null) {
               return ResponseEntity.badRequest().body("Due date is required");
           }

           LocalDateTime today = LocalDateTime.now();
           if (task.getDueDate().isBefore(today)) {
               return ResponseEntity.badRequest()
                       .body("The due date cannot be earlier than today.");
           }

           if (task.getStatus() == null) {
               task.setStatus(TaskStatus.PENDING);
           }
           task.setCreatedAt(LocalDateTime.now());

           Task savedTask = taskService.addTask(task);
           session.setAttribute("successMessage", "Task added successfully!");
           return ResponseEntity.ok(savedTask);

       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Failed to add task: " + e.getMessage());
       }
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
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Integer id, @Valid @RequestBody Task taskDetails, HttpSession session) {

        return taskService.findById(id).map(existingTask -> {

            existingTask.setTitle(taskDetails.getTitle());
            existingTask.setDescription(taskDetails.getDescription());
            existingTask.setDueDate(taskDetails.getDueDate());
            existingTask.setStatus(taskDetails.getStatus());
            existingTask.setPriority(taskDetails.getPriority());

            // 3. Save the unwrapped entity
            taskService.updateTask(existingTask);

            session.setAttribute("successMessage", "Task updated successfully!");
            return ResponseEntity.ok().build();

        }).orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        if(taskService.findById(id).isEmpty()) return ResponseEntity.notFound().build();
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/due-today")
    public ResponseEntity<List<Task>>getTaskDueToday(){
        return ResponseEntity.ok(taskService.getTasksDueToday());
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
