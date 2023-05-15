package com.example.projectmanagement.Service;
import com.example.projectmanagement.DTO.*;
import com.example.projectmanagement.Domaine.*;
import com.example.projectmanagement.Reposirtory.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import org.springframework.security.access.AccessDeniedException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectImplServ implements ProjectServ{
    @Autowired
    private EntityManager entityManager;
   @Autowired
    private  ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private  ActivityRepository activityRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TeamRepository teamRepository;


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
            projectDto.setProjectManagerEmail(project.getProjectManager().getEmail());
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
            if (project.getProjectManager() != null) {
                projectDto.setProjectManagerEmail(project.getProjectManager().getEmail());
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


    public Project createProject(ProjectRequest projectRequest) {
        String email = projectRequest.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        if (!user.hasProjectManagerRole()) {
            throw new AccessDeniedException("User does not have project manager role");
        }
        Long userId=projectRequest.getUserId();
        User user1=userRepository.findById(userId) .orElseThrow(() -> new EntityNotFoundException("User not found : " + userId));
        Project project = new Project();
        project.setProjectName(projectRequest.getProjectName());
        project.setDescriptionP(projectRequest.getDescriptionP());
        project.setObjectiveP(projectRequest.getObjectiveP());
        project.setProjectManager(user);
        project.setStatus("not started" );
        project.setBudget(projectRequest.getBudget());
        project.setDeadlineP(projectRequest.getDeadlineP());
        project.setAdmin(user1);

        return projectRepository.save(project);

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
        User user1=userRepository.findById(userId) .orElseThrow(() -> new EntityNotFoundException("User not found : " + userId));
        Project project = projectRepository.findById(projectRequest.getId()).orElseThrow(EntityNotFoundException::new);
        project.setProjectName(projectRequest.getProjectName());
        project.setProjectManager(user);
       project.setAdmin(user1);
       project.setDeadlineP(projectRequest.getDeadlineP());
       project.setObjectiveP(projectRequest.getObjectiveP());
       project.setDescriptionP(projectRequest.getDescriptionP());

       project.setBudget(projectRequest.getBudget());
        return projectRepository.save(project);
    }




    @Transactional
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // Supprimer les activités liées
        List<Activity> activities = project.getActivities();
        for (Activity activity : activities) {
            deleteActivity(activity);
        }

        // Supprimer le projet lui-même
        projectRepository.delete(project);
    }
    @Transactional
    private void deleteActivity(Activity activity) {
        // Supprimer les tâches liées
        Set<Task> tasks = new HashSet<>(activity.getTask()); // Copie des tâches pour éviter ConcurrentModificationException
        for (Task task : tasks) {
            deleteTask(task);
        }

        // Supprimer l'activité elle-même
        activity.getTask().clear(); // Dissocier les tâches de l'activité
        activity.setTeam(null);
        activityRepository.delete(activity);
    }

    private void deleteTask(Task task) {
        // Supprimer la tâche elle-même
        taskRepository.delete(task);
    }

}







