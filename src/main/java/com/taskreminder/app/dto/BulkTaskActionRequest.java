package com.taskreminder.app.dto;

import java.util.List;

public class BulkTaskActionRequest {
    private List<Integer> taskIds;

    public List<Integer> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Integer> taskIds) {
        this.taskIds = taskIds;
    }
}