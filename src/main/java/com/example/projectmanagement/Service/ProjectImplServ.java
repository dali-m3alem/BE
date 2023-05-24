package com.example.projectmanagement.Service;
import com.example.projectmanagement.DTO.*;
import com.example.projectmanagement.Domaine.*;
import com.example.projectmanagement.Reposirtory.ActivityRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import com.example.projectmanagement.Reposirtory.ProjectRepository;
import com.example.projectmanagement.Reposirtory.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;





@Service
@RequiredArgsConstructor
public class ProjectImplServ implements ProjectServ{
    private final EntityManager entityManager;

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final WebSocketHandler webSocketHandler;
    private final ActivityRepository activityRepository;
    private final ActitvtyServ actitvtyServ;

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
            projectDto.setDurationP(project.getDurationP());
            projectDto.setDeadlineP(project.getDeadlineP());
            projectDto.setProjectManagerEmail(project.getProjectManager().getEmail());
            projectDto.setStatus(changeProjectStatus(project.getId()));
            projectDto.setBudget(project.getBudget());

            return projectDto;
        }).collect(Collectors.toList());
    }

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
            projectDto.setDurationP(project.getDurationP());
            projectDto.setDeadlineP(project.getDeadlineP());
            projectDto.setProjectManagerEmail(project.getProjectManager().getEmail());
            projectDto.setStatus(changeProjectStatus(project.getId()));
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
        project.setDurationP(projectRequest.getDurationP());
        project.setProjectManager(user);
        project.setStatus("not started");
        project.setBudget(projectRequest.getBudget());
        project.setDeadlineP(projectRequest.getDeadlineP());
        project.setAdmin(user1);

        project = projectRepository.save(project);

        try {
            Notification notification = webSocketHandler
                    .createNotification("A new project has been created", user);
            webSocketHandler.sendNotification(notification);
            logger.info("Notification sent for project: {}", project.getProjectName());

        } catch (IOException e) {
            // Handle the exception
        }
        return project;
    }


    public void addProjectWithActivities(ProjectAndActivitiesDto projectAndActivitiesDto) {
        ProjectDto projectDto = projectAndActivitiesDto.getProjectDto();
        List<ActivityDto> activityDtos = projectAndActivitiesDto.getActivityDtos();
        //...
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
            logger.info("Notification sent for project: {}", project.getProjectName());

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

        // Delete the project
        projectRepository.deleteById(id);

        // Send a notification to the project manager
        try {
            Notification notification = webSocketHandler
                    .createNotification("Project has been deleted", projectManager);
            webSocketHandler.sendNotification(notification);
            logger.info("Notification sent for project deletion: {}", project.getProjectName());
        } catch (IOException e) {
            // Handle the exception
        }
    }
    public String changeProjectStatus(Long id) {
        Project project = getProjectById(id);
        List<Activity> activities = actitvtyServ.getActivityByProjectId(id);
        boolean allActivitiesDone = true;

        for (Activity activity : activities) {
            if (!activity.getStatus().equals("DONE")) {
                allActivitiesDone = false;
                break;
            }
        }

        if (allActivitiesDone) {
            project.setStatus("DONE");
        }

        projectRepository.save(project);
        return project.getStatus();
    }

  /*  public void saveProject(Project project) {
        projectRepository.save(project);
    }*/


    private static final Logger logger = LoggerFactory.getLogger(Project.class);
}
