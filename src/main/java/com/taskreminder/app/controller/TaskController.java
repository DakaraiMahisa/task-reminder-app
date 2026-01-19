package com.taskreminder.app.controller;
import com.taskreminder.app.entity.Task;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.service.EmailService;
import com.taskreminder.app.service.ExportService;
import com.taskreminder.app.security.CustomUserDetails;
import com.taskreminder.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.taskreminder.app.service.TaskService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ExportService exportService;

    @Autowired
    private UserService userService;

    @GetMapping("/tasks")
    public String listTasks(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5")int size,
            @RequestParam(required = false) List<String> filters,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "table") String view,
            Model model) {

        List<String> activeFilters = (filters == null) ? List.of() : filters;

        boolean filterRequest = !activeFilters.isEmpty();

        model.addAttribute("filters", activeFilters);
        model.addAttribute("status", status);
        model.addAttribute("priority", priority);
        model.addAttribute("title", title);
        model.addAttribute("view", view);
        model.addAttribute("sortBy", sortBy);

        if (filterRequest) {

            if (activeFilters.contains("status") && status == null) {
                model.addAttribute("error", "Please select a status");
                return "tasks";
            }

            if (activeFilters.contains("priority") && priority == null) {
                model.addAttribute("error", "Please select a priority");
                return "tasks";
            }

            if (activeFilters.contains("title")
                    && (title == null || title.isBlank())) {
                model.addAttribute("error", "Please enter a title");
                return "tasks";
            }
        }

        TaskStatus effectiveStatus = activeFilters.contains("status") ? status : null;

        TaskPriority effectivePriority = activeFilters.contains("priority") ? priority : null;

        String effectiveTitle = activeFilters.contains("title") ? title : null;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Task> taskPage = taskService.getPagedTasks(pageable, effectiveStatus, effectivePriority, effectiveTitle);
        model.addAttribute("taskPage",taskPage);
        model.addAttribute("tasks",taskPage.getContent());
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages",taskPage.getTotalPages());
        model.addAttribute("size",size);

        long totalTasks =
                taskService.countFilteredTasks(
                        effectiveStatus,
                        effectivePriority,
                        effectiveTitle
                );

        long completedTasks =
                taskService.countCompletedTasks(
                        effectiveStatus,
                        effectivePriority,
                        effectiveTitle
                );


        long pendingTasks = totalTasks - completedTasks;

        int progressPercent = totalTasks == 0
                ? 0
                : (int) ((completedTasks * 100) / totalTasks);

        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("pendingTasks", pendingTasks);
        model.addAttribute("progressPercent", progressPercent);

        int totalPages = taskPage.getTotalPages();
        List<Integer> pageNumbers = IntStream.range(0,totalPages).boxed().toList();
        model.addAttribute("pageNumbers",pageNumbers);
        model.addAttribute("pageContext", "tasks");

        List<Task> dueTodayTasks = taskService.getTasksDueToday();

        model.addAttribute("dueTodayTasks", dueTodayTasks);
        model.addAttribute("dueTodayCount", dueTodayTasks.size());

        return "tasks";
    }

    @GetMapping("/tasks/add")
    public String addForm(Model model){
        model.addAttribute("task",new Task());
        return "add-task";
    }


    @PostMapping("/tasks/add")
    public String addTask(@Valid @ModelAttribute("task")Task task,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,Model model){
        if(result.hasErrors()){
            return "add-task";
        }

        if(task.getDueDate()==null){
            model.addAttribute("errorMessage","Due date is required");
            model.addAttribute("task",task);
            return "add-task";
        }
        if(task.getStatus()==null){
            task.setStatus(TaskStatus.PENDING);
        }
        try{
            LocalDateTime today = LocalDateTime.now();
            if (task.getDueDate().isBefore(today)) {
                throw new IllegalArgumentException("The due date cannot be earlier than today (" + today + ").");
            }
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


    @GetMapping("tasks/update/{id}")
    public String editForm(@PathVariable int id, Model model){
        Task task = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        model.addAttribute("task",task);
        return "edit-task";
    }


    @PostMapping("/tasks/update")
    public String updateTask(@Valid @ModelAttribute("task") Task task,
                             BindingResult result,RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "edit-task";
        }
        try {
            Task existing = taskService.findById(task.getId())
                    .orElseThrow(() -> new RuntimeException("Task not found"));

            task.setCreatedAt(existing.getCreatedAt());
                if(task.getStatus()==TaskStatus.DONE){
                    task.setCompletedAt(LocalDateTime.now());
                }
                taskService.updateTask(task);
                redirectAttributes.addFlashAttribute(
                        "successMessage", "Task updated successfully!"
                );

        }catch(Exception e){
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Failed to update task. Please try again."
            );
        }


        return "redirect:/api/tasks";
    }

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
        return "redirect:/api/tasks";
    }

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

    @GetMapping("/tasks/calendar")
    @ResponseBody
    public List<Map<String, Object>> getTasksForCalendar() {

        List<Task> tasks = taskService.findAllForCurrentUser();

        return tasks.stream().map(task -> {
            Map<String, Object> event = new HashMap<>();
            event.put("id", task.getId());
            event.put("title", task.getTitle());
            event.put("start", task.getDueDate().toString());
            event.put("status", task.getStatus().name());

            switch (task.getStatus()) {
                case DONE -> event.put("color", "#22c55e");
                case IN_PROGRESS -> event.put("color", "#3b82f6");
                default -> event.put("color", "#f97316");
            }

            return event;
        }).toList();
    }
    @PatchMapping("/tasks/{id}/status")
    @ResponseBody
    public ResponseEntity<Void> updateTaskStatus(
            @PathVariable Integer id,
            @RequestParam TaskStatus status
    ) {
        taskService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/tasks/{id}/due-date")
    @ResponseBody
    public ResponseEntity<Void> updateTaskDueDate(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload
    ) {
        String dueDateStr = payload.get("dueDate");

        if (dueDateStr == null || dueDateStr.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        LocalDateTime dueDate;
        try {
            dueDate = LocalDateTime.parse(dueDateStr);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build();
        }
        taskService.updateDueDateForCurrentUser(id, dueDate);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportTasks(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "download") String action) throws Exception {
        List<Task> tasks = taskService.findAllForCurrentUser();
        byte[] csvBytes = exportService.generateTasksCsv(tasks);
        String fileName = "tasks_report.csv";

        if ("email".equalsIgnoreCase(action)) {
            String body = "<h3>Your Task Report</h3><p>Please find your exported tasks attached as a CSV file.</p>";
            emailService.sendEmailWithAttachment(userDetails.getUsername(), "Your Task Report", body, csvBytes, fileName);

            return ResponseEntity.ok().body("Email Sent".getBytes());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }
}
