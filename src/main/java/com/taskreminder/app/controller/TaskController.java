package com.taskreminder.app.controller;
import com.taskreminder.app.entity.Task;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.taskreminder.app.service.TaskService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Comparator;

@Controller
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private TaskService taskService;


    @GetMapping("/tasks")
    public String listTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        // Start with full list
        List<Task> tasks = taskService.getAllTasks();

        // Apply filters
        if (status != null) {
            tasks = tasks.stream()
                    .filter(t -> t.getStatus() == status)
                    .toList();
        }

        if (priority != null) {
            tasks = tasks.stream()
                    .filter(t -> t.getPriority() == priority)
                    .toList();
        }
        if (title != null && !title.isEmpty()) {
            tasks = tasks.stream()
                    .filter(t -> t.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .toList();
        }

        // Sorting
        Comparator<Task> comparator;
        switch (sortBy) {
            case "priority":
                comparator = Comparator.comparing(Task::getPriority);
                break;
            case "title":
                comparator = Comparator.comparing(Task::getTitle);
                break;
            default:
                comparator = Comparator.comparing(Task::getDueDate);
                break;
        }

        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        tasks = tasks.stream()
                .sorted(comparator)
                .toList();


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
                          RedirectAttributes redirectAttributes,Model model){
        if(result.hasErrors()){
            return "add-task";
        }

        if(task.getDueDate()==null||task.getDueDate().trim().isEmpty()){
            model.addAttribute("errorMessage","Due date is required");
            model.addAttribute("task",task);
            return "add-task";
        }
        if(task.getStatus()==null){
            task.setStatus(TaskStatus.PENDING);
        }
        try{
        task.setCreatedAt(LocalDateTime.now());
        taskService.addTask(task);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Task added successfully!"
            );

        }catch(Exception e){
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Failed to add task. Please try again."
            );
        }
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
                             BindingResult result,RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "edit-task";
        }
        try {
            Task existing = taskService.findById(task.getId()).orElse(null);
            if (existing != null) {
                task.setCreatedAt(existing.getCreatedAt());
                taskService.updateTask(task);
                redirectAttributes.addFlashAttribute(
                        "successMessage", "Task updated successfully!"
                );
            }
        }catch(Exception e){
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Failed to update task. Please try again."
            );
        }


        return "redirect:/api/tasks";
    }
//Mark as done method
    @GetMapping("/tasks/mark-done/{id}")
    public String markTaskAsDone(@PathVariable Integer id) {
        Task task  = taskService.findById(id).orElse(null);
        if(task!=null&&task.getStatus()!=TaskStatus.DONE){
            task.setStatus(TaskStatus.DONE);
            task.setCompletedAt(LocalDateTime.now());
            taskService.updateTask(task);
        }

        return "redirect:/api/tasks";
    }

    //Delete Task
    @GetMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable int id, RedirectAttributes redirectAttributes){
        try{
        taskService.deleteTask(id);
        redirectAttributes.addFlashAttribute(
                    "successMessage", "Task deleted successfully!");
        }catch(Exception e){
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Failed to delete task."
            );
        }
        return "redirect:/api/tasks ";
    }
//    View task api
    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable Integer id,Model model, RedirectAttributes redirectAttributes){

        Task task  = taskService.findById(id).orElse(null);
        if(task == null){
            redirectAttributes.addAttribute("errorMessage","Task not found");
            return "redirect:/api/tasks";
        }
        model.addAttribute("task",task);
        return "view-task";
    }

}
