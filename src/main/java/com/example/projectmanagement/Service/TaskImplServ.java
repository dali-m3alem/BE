package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.TaskDto;
import com.example.projectmanagement.Domaine.*;
import com.example.projectmanagement.Reposirtory.ActivityRepository;
import com.example.projectmanagement.Reposirtory.TaskRepository;
import com.example.projectmanagement.Reposirtory.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskImplServ implements TaskServ{




    private final TaskRepository taskRepository;
    private final UserRepository Repository;
    private final ActivityRepository activityRepository;
    private final NotificationHandler notificationHandler;

    @PersistenceContext
    private EntityManager entityManager;

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
        task.setCreatedBy(taskDto.getCreatedBy());
        task.setDueDate(taskDto.getDueDate());
        task.setUser(user);
        task.setActivity(activity);
        task.setStatus("todo");
        task.setManager(id);
        task = taskRepository.save(task);
        try {
            Notification notification = notificationHandler.createNotification("A new task has been created to you", user);
            notificationHandler.sendNotification(notification);
            logger.info("Notification sent for task: {}", task.getTitle());

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
                Notification notification = notificationHandler.createNotification("A task has been updated to you", user);
                notificationHandler.sendNotification(notification);
                logger.info("Notification sent for task: {}", updatedTask.getTitle());

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

        try {
            Notification notification = notificationHandler.createNotification("Task has been deleted", user);
            notificationHandler.sendNotification(notification);
            logger.info("Notification sent for task deletion: {}", task.getTitle());
        } catch (IOException e) {
        }taskRepository.deleteById(id);
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



  /*  @Transactional()
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

    private static final Logger logger = LoggerFactory.getLogger(Project.class);


}
