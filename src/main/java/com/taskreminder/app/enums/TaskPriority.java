package com.taskreminder.app.enums;

public enum TaskPriority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String label;

    TaskPriority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
