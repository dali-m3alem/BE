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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
public class TaskController {
    @Autowired
    private TaskImplServ taskservice;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private JwtService jwtService;

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
    public List<Task> getTasksByUserId2(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        System.out.println(jwt);
        Long userId = Long.valueOf(jwtService.extractId(jwt));

        return taskservice.getTasksByUserId(userId);
    }

    @GetMapping("/getTasksManager")
    public List<Task> getTasksByManagerId2(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        System.out.println(jwt);
        Long managerId = Long.valueOf(jwtService.extractId(jwt));

        return taskservice.getTasksByManagerId(managerId);
    }
    @PreAuthorize("hasAuthority('manager')")
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
    @PreAuthorize("hasAuthority('manager') || hasAuthority('user')")
    @PutMapping("/update")
    public ResponseEntity<Task> updateTask(@RequestBody Task task) {
        Task updatedTask = taskservice.updateTask1(task);
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }
    @PreAuthorize("hasAuthority('manager') || hasAuthority('user')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto) {
        Task updatedTask = taskservice.updateTask(taskDto, id);
        return ResponseEntity.ok(updatedTask);
    }

    @PreAuthorize("hasAuthority('manager')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskservice.deleteTask(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/getAllTasks")
    public List<Task> getAllUsers() {
        return taskservice.getAllTasks();
    }


    @GetMapping("/getAllTasks/{str}")
    public List<Task> getAllTasksOfAuthenticatedUser(@PathVariable("str") String str) {
        return taskservice.getAllTasksOfUser(str);
    }

    @GetMapping("/tasks")
    public List<Task> getTasksByUser(@RequestParam(name = "user_id") Long userId) {
        return taskRepository.findByUserId(userId);
    }

    @GetMapping("/countTasks")
    public ResponseEntity<Long> countTask() {
        Long countTask = taskservice.countTask();
        return ResponseEntity.ok(countTask);
    }

    @GetMapping("/taskCount/{status}")
    public ResponseEntity<Long> countTasksByStatus(@PathVariable String status) {
        Long count = taskservice.countTasksByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/TaskPercent/{state}")
    public ResponseEntity<Integer> calculatePercentByState(@PathVariable String state) {
        Long totalTasks = taskservice.countTask();
        Long stateTasks = taskservice.countTasksByStatus(state);

        if (totalTasks == 0) {
            return ResponseEntity.badRequest().body(null);
        }

        int percent = (int) Math.round((stateTasks.doubleValue() / totalTasks.doubleValue()) * 100.0);

        return ResponseEntity.ok(percent);
    }
}
