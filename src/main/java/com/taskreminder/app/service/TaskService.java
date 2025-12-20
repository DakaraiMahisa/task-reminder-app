package com.taskreminder.app.service;
import com.taskreminder.app.entity.Task;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

   public List<Task> getAllTasks(){
        return taskRepository.findAll();
   }

    public Task addTask(Task task){
        return taskRepository.save(task);
    }

    public Task updateTask(Task task){
        return taskRepository.save(task);
    }

    public void deleteTask(Integer id){
        taskRepository.deleteById(id);
    }

    public Optional<Task> findById(Integer id){

        return taskRepository.findById(id);
    }


//Filters with pagination + sorting
    public Page<Task> getPagedTasks(Pageable pageable, TaskStatus status, TaskPriority priority,String keyword){
       if(status!=null){
           return taskRepository.findByStatus(status,pageable);
       }else if(priority!=null){
           return taskRepository.findByPriority(priority,pageable);
       }else if(keyword!=null){
           return taskRepository.findByTitleContainingIgnoreCase(keyword,pageable);
       }
        return taskRepository.findAll(pageable);
    }

    public Page<Task> findAll(Pageable pageable) {
       return taskRepository.findAll(pageable);
    }
}
