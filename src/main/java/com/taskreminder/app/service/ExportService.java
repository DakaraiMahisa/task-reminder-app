package com.taskreminder.app.service;

import com.opencsv.CSVWriter;
import com.taskreminder.app.entity.Task;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Optional;

@Service
public class ExportService {
    public byte[] generateTasksCsv(List<Task> tasks) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {

            writer.writeNext(new String[]{"ID", "Title", "Description", "Due Date", "Status", "Priority", "Created At", "Completed At"});

            for (Task task : tasks) {
                writer.writeNext(new String[]{
                        String.valueOf(task.getId()),
                        task.getTitle(),
                        task.getDescription(),
                        task.getDueDate() != null ? task.getDueDate().toString() : "",
                        task.getStatus().toString(),
                        task.getPriority().toString(),
                        task.getCreatedAt().toString(),
                        task.getCompletedAt() != null ? task.getCompletedAt().toString() : "N/A"
                });
            }
        }
        return out.toByteArray();
    }
}
