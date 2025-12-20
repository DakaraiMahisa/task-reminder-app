package com.taskreminder.app.controller;
import com.taskreminder.app.entity.Task;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.taskreminder.app.service.TaskService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private TaskService taskService;


    @GetMapping("/tasks")
    public String listTasks(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5")int size,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            Model model) {

        Sort sort = Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task>taskPage  =  taskService.getPagedTasks(pageable,status,priority,title);
        model.addAttribute("taskPage",taskPage);
        model.addAttribute("tasks",taskPage.getContent());
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages",taskPage.getTotalPages());
        model.addAttribute("size",size);

        int totalPages = taskPage.getTotalPages();
        List<Integer> pageNumbers = IntStream.range(0,totalPages).boxed().toList();
        model.addAttribute("pageNumbers",pageNumbers);

        model.addAttribute("sortBy",sortBy);
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
                if(task.getStatus()==TaskStatus.DONE){
                    task.setCompletedAt(LocalDateTime.now());
                }
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
