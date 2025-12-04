package com.taskreminder.app.controller;
import com.taskreminder.app.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.taskreminder.app.service.TaskService;
import org.springframework.web.bind.annotation.*;
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
    public String addTask(Task task){
        taskService.addTask(task);
        return "redirect:/api/tasks";
    }

    //Show edit form
    @GetMapping("tasks/edit/{id}")
    public String editForm(@PathVariable int id, Model model){
        model.addAttribute("task",taskService.getTaskById(id));
        return "edit-task";
    }

     //Handling edit submission
    @PostMapping("/tasks/update")
    public String updateTask(Task task){
        taskService.updateTask(task);
        return "redirect:/api/tasks";
    }

    //Delete Task
    @GetMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable int id){
        taskService.deleteTask(id);
        return "redirect:/api/tasks ";
    }

}
