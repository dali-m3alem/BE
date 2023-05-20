package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.ActivityDto;
import com.example.projectmanagement.DTO.TaskDto;
import com.example.projectmanagement.DTO.UserDto;
import com.example.projectmanagement.Domaine.*;
import com.example.projectmanagement.Reposirtory.ActivityRepository;
import com.example.projectmanagement.Reposirtory.NotificationRepository;
import com.example.projectmanagement.Reposirtory.TaskRepository;
import com.example.projectmanagement.Reposirtory.UserRepository;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class TaskImplServ implements TaskServ{



    private final TaskRepository taskRepository;
    private final UserRepository Repository;
    private final ActivityRepository activityRepository;
    private final NotificationRepository notificationRepository;
    private final WebSocketHandler webSocketHandler;


    @PersistenceContext
    private EntityManager entityManager;
    public int countTasksNotDone() {
        return taskRepository.countTasksNotDone();
    }


    public List<Task> getTasksByActivityAndProjectAndManager(Long activityId, Long projectId, Long projectManagerId) throws Exception {
        List<Task> tasks = taskRepository.getTasksByActivityAndProjectAndManager(activityId, projectId, projectManagerId);
        if (tasks == null) {
            throw new Exception("No tasks found");
        }
        return tasks;
    }


    public List<Task> getTasksByUserId(Long userId) {
        String query = "SELECT t FROM Task t LEFT JOIN FETCH t.activity LEFT JOIN FETCH t.user u WHERE u.id = :userId\n";
        TypedQuery<Task> typedQuery = entityManager.createQuery(query, Task.class);
        typedQuery.setParameter("userId", userId);
        return typedQuery.getResultList();
    }
    public List<Task> getTasksByManagerId(Long managerId) {
        String query = "SELECT t FROM Task t LEFT JOIN FETCH t.activity LEFT JOIN FETCH t.manager u WHERE u.id = :managerId\n";
        TypedQuery<Task> typedQuery = entityManager.createQuery(query, Task.class);
        typedQuery.setParameter("managerId", managerId);
        return typedQuery.getResultList();
    }

    //we add notification each time we create task for user





    public Task createTask(TaskDto taskDto) {
        String email = taskDto.getEmail();
        User user = Repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        Activity activity = activityRepository.findById(taskDto.getActivity()).orElseThrow(()
                -> new IllegalArgumentException("Invalid activity id"));
        Long manager = taskDto.getManager();
        User id = Repository.findById(manager)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setDueDate(taskDto.getDueDate());
        task.setUser(user);
        task.setActivity(activity);
        task.setStatus("todo");
        task.setManager(id);
        task = taskRepository.save(task);
        String nameTask=taskDto.getTitle();
        try {
            Notification notification = webSocketHandler.createNotification("A new task"+nameTask+" has been created to you", user);
            webSocketHandler.sendNotification(notification);

        } catch (IOException e) {
            // Handle the exception
        }

        return task;
    }

    public Task updateTask(TaskDto taskDto, Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task updatedTask = optionalTask.get();
            updatedTask.setDescription(taskDto.getDescription());
            updatedTask.setTitle(taskDto.getTitle());
            updatedTask.setDueDate(taskDto.getDueDate());

            String email = taskDto.getEmail();
            User user = Repository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
            Activity activity = activityRepository.findById(taskDto.getActivity())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid activity id"));

            updatedTask.setUser(user);
            updatedTask.setActivity(activity);

            updatedTask= taskRepository.save(updatedTask);

            try {
                Notification notification = webSocketHandler.createNotification("A task has been updated to you", user);
                webSocketHandler.sendNotification(notification);

            } catch (IOException e) {
                // Handle the exception
            }

            return updatedTask;

        } else {
            throw new NotFoundException("Task not found with id: " + id);
        }

    }

    public class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }


    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("task not found with id: " + id));
        User user = task.getUser();
        taskRepository.deleteById(id);

        try {
            Notification notification = webSocketHandler.createNotification("Task has been deleted", user);
            webSocketHandler.sendNotification(notification);
        } catch (IOException e) {
        }
    }



    public Task updateTask1(Task task) {
        Optional<Task> optionalTask = taskRepository.findById(task.getId());
        if (optionalTask.isPresent()) {
            Task updatedTask = optionalTask.get();
            updatedTask.setStatus(task.getStatus());
            return taskRepository.save(updatedTask);
        } else {
            throw new NotFoundException("Task not found with id: " + task.getId());
        }
    }


    public List<Task> getAllTasks() {
        return (List<Task>) taskRepository.findAll();
    }
    public List<Task> getAllTasksOfUser(String email) {
        User user = Repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getTasks();
    }

   // public List<Task> getTasksByUserId(Long userId) {
    //    User user = new User();
    //    user.setId(userId);
    //    return taskRepository.findByUser(user);
    //}



 /*   @Transactional()
    public List<Task> getAllTasksWithUserAndActivity() {
        List<Task> tasks = taskRepository.findAll();
        for (Task task : tasks) {
            User user = task.getUser();
            if (user != null) {
                user.getTasks().size(); // Load tasks for user
            }
            Activity activity = task.getActivity();
            if (activity != null) {
                activity.getTasks().size(); // Load tasks for activity
            }
        }
        return tasks;
    }*/



}
