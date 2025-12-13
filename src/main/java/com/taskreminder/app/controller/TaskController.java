package com.taskreminder.app.controller;
import com.taskreminder.app.entity.Task;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.taskreminder.app.service.TaskService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /*
    //Show all tasks
    @GetMapping("/tasks")
    public String listTasks(Model model){
        model.addAttribute("tasks",taskService.getAllTasks());
        return "tasks";
    }*/
    @GetMapping("/tasks")
    public String listTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        // Start with full list
        List<Task> tasks = taskService.getAllTasks();

        // Apply filters (combined)
        if (status != null && !status.isEmpty()) {
            tasks = tasks.stream()
                    .filter(t -> t.getStatus().equalsIgnoreCase(status))
                    .toList();
        }

        if (priority != null && !priority.isEmpty()) {
            tasks = tasks.stream()
                    .filter(t -> t.getPriority().equalsIgnoreCase(priority))
                    .toList();
        }

        if (title != null && !title.isEmpty()) {
            tasks = tasks.stream()
                    .filter(t -> t.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .toList();
        }

        // Sorting
        switch (sortBy) {
            case "priority":
                tasks = tasks.stream()
                        .sorted(Comparator.comparing(Task::getPriority))
                        .toList();
                break;

            case "title":
                tasks = tasks.stream()
                        .sorted(Comparator.comparing(Task::getTitle))
                        .toList();
                break;

            default:
                // Default sort by due date
                tasks = tasks.stream()
                        .sorted(Comparator.comparing(Task::getDueDate))
                        .toList();
                break;
        }

        // Apply sort direction
        if ("desc".equalsIgnoreCase(sortDir)) {
            Collections.reverse(tasks);
        }

        // Add to model for UI
        model.addAttribute("tasks", tasks);
        model.addAttribute("status", status);
        model.addAttribute("priority", priority);
        model.addAttribute("title", title);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "tasks";
    }



    //Show add form
    @GetMapping("/tasks/add")
    public String addForm(Model model){
        model.addAttribute("task",new Task());
        return "add-task";
    }

     //Handle add submission
    @PostMapping("/tasks/add")
    public String addTask(@Valid @ModelAttribute("task")Task task,
                          BindingResult result,
                          Model model){
        if(result.hasErrors()){
            return "add-task";
        }
        task.setCreatedAt(LocalDateTime.now());
        taskService.addTask(task);
        return "redirect:/api/tasks";
    }

    //Show edit form
    @GetMapping("tasks/update/{id}")
    public String editForm(@PathVariable int id, Model model){
        Task task = taskService.findById(id).orElse(null);
        if(task == null){
            return "redirect:/api/tasks";
        }
        model.addAttribute("task",task);
        return "edit-task";
    }

     //Handling edit submission
    @PostMapping("/tasks/update")
    public String updateTask(@Valid @ModelAttribute("task") Task task,
                             BindingResult result,Model model){
        if(result.hasErrors()){
            return "edit-task";
        }
        Task existing = taskService.findById(task.getId()).orElse(null);
        if(existing!=null){
        task.setCreatedAt(existing.getCreatedAt());
        taskService.updateTask(task);

        }
        return "redirect:/api/tasks";
    }
//Mark as done method
    @GetMapping("/tasks/mark-done/{id}")
    public String markTaskAsDone(@PathVariable Integer id) {

        taskService.findById(id).ifPresent(task -> {
            task.setStatus("Completed");
            taskService.updateTask(task);
        });

        return "redirect:/api/tasks";
    }

    //Delete Task
    @GetMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable int id){
        taskService.deleteTask(id);
        return "redirect:/api/tasks ";
    }


}
