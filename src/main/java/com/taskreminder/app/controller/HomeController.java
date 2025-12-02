package com.taskreminder.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.taskreminder.app.service.TaskService;

@Controller
public class HomeController {
    private final TaskService taskService; // instance, not static

    // Constructor injection
    public HomeController(TaskService taskService) {
        this.taskService = taskService;
    }
    @GetMapping("/tasks")
    public String index(Model model){
        model.addAttribute("message","Welcome to the Task Reminder app! By Dakarai");
        model.addAttribute("tasks",taskService.getAllTasks());
        return "tasks";

    }
}
