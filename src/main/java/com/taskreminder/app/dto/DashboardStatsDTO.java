package com.taskreminder.app.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardStatsDTO {
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long overdueTasks;
    private double completionRate;
}
