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

     //Show all tasks
    @GetMapping("/tasks")
    public String listTasks(Model model){
        model.addAttribute("tasks",taskService.getAllTasks());
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
