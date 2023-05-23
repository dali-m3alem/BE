package com.example.projectmanagement.Service;
import com.example.projectmanagement.DTO.*;
import com.example.projectmanagement.Domaine.*;
import com.example.projectmanagement.Reposirtory.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectImplServ implements ProjectServ{

    private final EntityManager entityManager;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final ActivityRepository activityRepository;
    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;
    private final NotificationRepository notificationRepository;
    private final WebSocketHandler webSocketHandler;


    public List<Project> getAllProjects() {

        return projectRepository.findAll();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }
    public List<ProjectDto> getAllProjectsByManagerId(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
        List<Project> projects = projectRepository.findByProjectManager(manager);
        return projects.stream().map(project -> {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setId(project.getId());
            projectDto.setProjectName(project.getProjectName());
            projectDto.setDescriptionP(project.getDescriptionP());
            projectDto.setObjectiveP(project.getObjectiveP());
            projectDto.setAdmin(project.getAdmin().getEmail());
            projectDto.setDeadlineP(project.getDeadlineP());
            projectDto.setProjectManagerEmail(project.getProjectManager());
            projectDto.setActivity((List<Activity>) project.getActivity());
            projectDto.setStatus(project.getStatus());
            projectDto.setBudget(project.getBudget());

            return projectDto;
        }).collect(Collectors.toList());
    }
    @Transactional
    public List<ProjectDto> getAllProjectsByAdminId(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
        List<Project> projects = projectRepository.findByAdmin(admin);
        return projects.stream().map(project -> {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setId(project.getId());
            projectDto.setProjectName(project.getProjectName());
            projectDto.setDescriptionP(project.getDescriptionP());
            projectDto.setObjectiveP(project.getObjectiveP());
            projectDto.setAdminId(project.getAdmin().getId());
            projectDto.setDeadlineP(project.getDeadlineP());
            projectDto.setActivity( project.getActivity());

            if (project.getProjectManager() != null) {
                projectDto.setProjectManagerEmail(project.getProjectManager());
            } else {
                projectDto.setProjectManagerEmail(null); // or any other default value
            }
            projectDto.setStatus(project.getStatus());
            projectDto.setBudget(project.getBudget());

            return projectDto;
        }).collect(Collectors.toList());
    }

    public Long countProjects() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Query query = entityManager.createQuery("SELECT COUNT(p) FROM Project p");
        Long count = (Long) query.getSingleResult();
        entityManager.close();
        return count;
    }


    public Optional<Long> getTotalBudget() {
        List<Project> projects = projectRepository.findAll();
        if (projects != null && !projects.isEmpty()) {
            return Optional.of(projects.stream()
                    .filter(project -> project.getBudget() != null)
                    .mapToLong(Project::getBudget)
                    .sum());
        }
        return Optional.empty();
    }


    public void addProjectWithActivities(ProjectAndActivitiesDto projectAndActivitiesDto) {
        ProjectDto projectDto = projectAndActivitiesDto.getProjectDto();
        List<ActivityDto> activityDtos = projectAndActivitiesDto.getActivityDtos();
        //...
    }
    @Override
    public Project createProject(ProjectRequest projectRequest) {
        String email = projectRequest.getEmail();
        User user = userRepository.findByEmail(email).orElseThrow(()
                -> new EntityNotFoundException("User not found with email: " + email));

        if (!user.hasProjectManagerRole()) {
            throw new AccessDeniedException("User does not have project manager role");
        }

        Long userId = projectRequest.getUserId();
        User user1 = userRepository.findById(userId).orElseThrow(()
                -> new EntityNotFoundException("User not found : " + userId));
        Project project = new Project();
        project.setProjectName(projectRequest.getProjectName());
        project.setDescriptionP(projectRequest.getDescriptionP());
        project.setObjectiveP(projectRequest.getObjectiveP());
        project.setProjectManager(user);
        project.setStatus("not started");
        project.setBudget(projectRequest.getBudget());
        project.setDeadlineP(projectRequest.getDeadlineP());
        project.setAdmin(user1);
        String projectName = projectRequest.getProjectName();
        project = projectRepository.save(project);

        try {
            String notificationContent = "A new project " + projectName + " has been created. Please submit your planning as soon as possible.";
            Notification notification = webSocketHandler.createNotification(notificationContent, user);
            webSocketHandler.sendNotification(notification);

        } catch (IOException e) {
            // Handle the exception
        }
        return project;
    }
    public Project updateProject( ProjectRequest projectRequest) {
        String email = projectRequest.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        if (!user.hasProjectManagerRole()) {
            throw new AccessDeniedException("User does not have project manager role");
        }
        Long userId=projectRequest.getUserId();
        User user1=userRepository.findById(userId) .
                orElseThrow(()
                        -> new EntityNotFoundException("User not found : " + userId));
        Project project = projectRepository
                .findById(projectRequest.getId())
                .orElseThrow(EntityNotFoundException::new);
        project.setProjectName(projectRequest.getProjectName());
        project.setProjectManager(user);
        project.setAdmin(user1);
        project.setDeadlineP(projectRequest.getDeadlineP());
        project.setObjectiveP(projectRequest.getObjectiveP());
        project.setDescriptionP(projectRequest.getDescriptionP());
        project.setBudget(projectRequest.getBudget());
        project= projectRepository.save(project);

        try {
            Notification notification = webSocketHandler
                    .createNotification("Project has been updated", user);
            webSocketHandler.sendNotification(notification);
        } catch (IOException e) {
            // Handle the exception
        }

        return project;
    }


    public void deleteProject(Long id) {


        // Retrieve the project to be deleted
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));

        // Get the project manager's information
        User projectManager = project.getProjectManager();
        // Send a notification to the project manager
        try {
            Notification notification = webSocketHandler
                    .createNotification("Project has been deleted", projectManager);
            webSocketHandler.sendNotification(notification);
        } catch (IOException e) {
            // Handle the exception
        }

        // Delete the project
        projectRepository.deleteById(id);


    }



}







