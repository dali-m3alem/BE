package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.*;
import com.example.projectmanagement.Domaine.*;
import com.example.projectmanagement.Reposirtory.ActivityRepository;
import com.example.projectmanagement.Reposirtory.NotificationRepository;
import com.example.projectmanagement.Reposirtory.TaskRepository;
import com.example.projectmanagement.Reposirtory.UserRepository;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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


    public List<taskDTOSimple> getTasksByActivityAndProjectAndManager(Long activityId, Long projectId, Long projectManagerId) throws Exception {
        List<Task> tasks = taskRepository.getTasksByActivityAndProjectAndManager(activityId, projectId, projectManagerId);


        return tasks.stream().map(task -> {
            taskDTOSimple taskDTOSimple = new taskDTOSimple();
            taskDTOSimple.setId(task.getId());
            taskDTOSimple.setStatus(task.getStatus());
            taskDTOSimple.setTitle(task.getTitle());
            taskDTOSimple.setDescription(task.getDescription());
            taskDTOSimple.setUser(task.getUser());
            taskDTOSimple.setDueDate(task.getDueDate());
            taskDTOSimple.setActivity(task.getActivity());

            return taskDTOSimple;
        }).collect(Collectors.toList());
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
    public List<Task> getTaskByActivityId(Long activityId){
        String query = "SELECT t FROM Task t LEFT JOIN FETCH t.activity LEFT JOIN FETCH t.activity a WHERE a.id=:activityId\n";
        TypedQuery<Task> typedQuery=entityManager.createQuery(query,Task.class);
        typedQuery.setParameter("activityId",activityId);
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
            Notification notification = webSocketHandler.createNotification("A new task :"+nameTask+" has been created to you", user);
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
            updatedTask.setStatus(taskDto.getStatus());
            String email = taskDto.getEmail();
            User user = Repository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
            Activity activity = activityRepository.findById(taskDto.getActivity())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid activity id"));

            updatedTask.setUser(user);
            updatedTask.setActivity(activity);

            updatedTask= taskRepository.save(updatedTask);
            String nameTask=taskDto.getTitle();

            try {
                Notification notification = webSocketHandler.createNotification("This task :"+nameTask+"  has been updated to you", user);
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
        String nameTask=task.getTitle();

        try {
            Notification notification = webSocketHandler.createNotification("This task :"+nameTask+"  has been updated to you", user);
            webSocketHandler.sendNotification(notification);
        } catch (IOException e) {
        }
    }

    public Task updateTask1(Task task) {
        Optional<Task> optionalTask = taskRepository.findById(task.getId());
        if (optionalTask.isPresent()) {
            Task updatedTask = optionalTask.get();
        updatedTask.setStatus(updatedTask.getStatus());
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
    public Long countTask() {
        return taskRepository.count();
    }
    public Long countTasksByStatus(String status) {
        Query query = createCountQueryByStatus(status);
        Long count = (Long) query.getSingleResult();
        return count;
    }

    private Query createCountQueryByStatus(String status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Task> root = query.from(Task.class);

        query.select(cb.count(root));
        query.where(cb.equal(root.get("status"), status));

        return entityManager.createQuery(query);
    }


}
