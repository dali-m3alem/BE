package com.example.projectmanagement.resource;

import com.example.projectmanagement.DTO.TaskDto;
import com.example.projectmanagement.DTO.taskUP;
import com.example.projectmanagement.Domaine.Task;
import com.example.projectmanagement.Domaine.User;
import com.example.projectmanagement.Reposirtory.TaskRepository;
import com.example.projectmanagement.Service.TaskImplServ;
import com.example.projectmanagement.Service.TaskServ;
import com.example.projectmanagement.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
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
    @Autowired
    private  JwtService jwtService;

    @GetMapping("/countTask")
    public int countTasksNotDone() {
        return taskservice.countTasksNotDone();
    }
    @GetMapping("/getTasksByActivityAndProjectAndManager/{activityId}/{projectId}")
    public ResponseEntity<?> getTasksByActivityAndProjectAndManager(@PathVariable Long activityId, @PathVariable Long projectId, HttpServletRequest request) {
        try {
            final String authHeader = request.getHeader("Authorization");
            String jwt = authHeader.substring(7);
            System.out.println(jwt);
            Long managerId = Long.valueOf(jwtService.extractId(jwt));
            return ResponseEntity.status(HttpStatus.OK).body(taskservice.getTasksByActivityAndProjectAndManager(activityId, projectId, managerId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/getTasks")
    public List<Task> getTasksByUserId2(@RequestParam Long userId) {
        return taskservice.getTasksByUserId(userId);
    }
    @GetMapping("/getTasksManager")
    public List<Task> getTasksByManagerId2(@RequestParam Long managerId) {
        return taskservice.getTasksByManagerId(managerId);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTask(@RequestBody TaskDto taskDto, HttpServletRequest request) {
        try {
            final String authHeader = request.getHeader("Authorization");
            String jwt = authHeader.substring(7);
            Long userId = Long.valueOf(jwtService.extractId(jwt));
            List<String> roles = jwtService.extractRoles(jwt);
            System.out.println(roles);

            if (roles.contains("manager")) {
                taskDto.setManager(userId);
                Task newTask = taskservice.createTask(taskDto);
                return ResponseEntity.ok(newTask);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only managers can create tasks.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Task> updateTask(@RequestBody Task task) {
        Task updatedTask = taskservice.updateTask1(task);
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
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
