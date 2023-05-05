package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.ActivityDto;
import com.example.projectmanagement.DTO.TaskDto;
import com.example.projectmanagement.DTO.UserDto;
import com.example.projectmanagement.Domaine.Activity;
import com.example.projectmanagement.Domaine.Task;
import com.example.projectmanagement.Domaine.User;
import com.example.projectmanagement.Reposirtory.ActivityRepository;
import com.example.projectmanagement.Reposirtory.TaskRepository;
import com.example.projectmanagement.Reposirtory.UserRepository;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskImplServ implements TaskServ{



    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository Repository;
    @Autowired
    private ActivityRepository activityRepository;

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


    public Task createTask(TaskDto taskDto) {
        String email = taskDto.getEmail();
        User user = Repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        Activity activity= activityRepository.findById(taskDto.getActivity()).orElseThrow(()
                -> new IllegalArgumentException("Invalid activity id"));
        Long manager = taskDto.getManager();
        User id = Repository.findById(manager)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        Task task=new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setCreatedBy(taskDto.getCreatedBy());
        task.setDueDate(taskDto.getDueDate());
        task.setUser(user);
        task.setActivity(activity);
        task.setStatus("todo");
        task.setManager(id);
        return taskRepository.save(task);
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

            return taskRepository.save(updatedTask);
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
        taskRepository.deleteById(id);
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



    @Transactional()
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
    }



}
