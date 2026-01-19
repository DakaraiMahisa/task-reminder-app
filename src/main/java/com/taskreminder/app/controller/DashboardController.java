package com.taskreminder.app.controller;
import com.taskreminder.app.dto.DashboardStatsDTO;
import com.taskreminder.app.entity.Task;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.repository.TaskRepository;
import com.taskreminder.app.service.TaskService;
import com.taskreminder.app.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tools.jackson.databind.ObjectMapper;
import java.security.Principal;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("/api")
public class DashboardController {

    private final TaskService taskService;
    private final UserService userService;
    private final TaskRepository taskRepository;

    public DashboardController(TaskService taskService, UserService userService,TaskRepository taskRepository) {
        this.taskService = taskService;
        this.userService = userService;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal,@RequestParam(name = "days", defaultValue = "7") int days) {

        User user = userService.findByEmail(principal.getName());

        DashboardStatsDTO stats = taskService.getDashboardStats(user);

        List<Task> recentTasks = taskRepository.findFirst5ByUserOrderByDueDateDesc(user);

        Map<String, Long> trendData = taskService.getDailyStats(user,days);
        ObjectMapper mapper = new ObjectMapper();

        try {
            model.addAttribute("chartLabels", mapper.writeValueAsString(trendData.keySet()));
            model.addAttribute("chartData", mapper.writeValueAsString(trendData.values()));
        } catch (Exception e) {
            model.addAttribute("chartLabels", "[]");
            model.addAttribute("chartData", "[]");
        }

        model.addAttribute("stats", stats);
        model.addAttribute("userName", user.getName());
        model.addAttribute("recentTasks", recentTasks);
        model.addAttribute("pageContext", "dashboard");
        return "dashboard";
    }
}