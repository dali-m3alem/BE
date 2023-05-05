package com.example.projectmanagement.resource;

import com.example.projectmanagement.DTO.TaskDto;
import com.example.projectmanagement.Domaine.Task;
import com.example.projectmanagement.Domaine.User;
import com.example.projectmanagement.Reposirtory.TaskRepository;
import com.example.projectmanagement.Service.TaskImplServ;
import com.example.projectmanagement.Service.TaskServ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
public class TaskController {
    @Autowired
    private TaskImplServ taskservice;
    @Autowired
    private TaskRepository taskRepository;



    @GetMapping("/getTasks")
    public List<Task> getTasksByUserId2(@RequestParam Long userId) {
        return taskservice.getTasksByUserId(userId);
    }
    @GetMapping("/getTasksManager")
    public List<Task> getTasksByManagerId2(@RequestParam Long managerId) {
        return taskservice.getTasksByManagerId(managerId);
    }

    @PostMapping("/CreateTask")
    public ResponseEntity<Task> createTask(@RequestBody TaskDto taskDto) {
        Task newTask = taskservice.createTask(taskDto);
        return ResponseEntity.ok(newTask);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto) {
        Task updatedTask = taskservice.updateTask(taskDto, id);
        return ResponseEntity.ok(updatedTask);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskservice.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getAllTasks")
    public List<Task> getAllUsers() {
        return taskservice.getAllTasks();
    }


     @GetMapping( "/getAllTasks/{str}")
     public List<Task> getAllTasksOfAuthenticatedUser(@PathVariable("str") String str) {

         return taskservice.getAllTasksOfUser(str);
     }
    @GetMapping("/tasks")
    public List<Task> getTasksByUser(@RequestParam(name = "user_id") Long userId) {
        return taskRepository.findByUserId(userId);
    }




    
}
